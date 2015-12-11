package com.usefullc.solm.common.proxy.nio;

import com.usefullc.solm.common.proxy.nio.container.Channel;
import com.usefullc.solm.common.proxy.nio.container.Key;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by shengshan.tang on 2015/12/7 at 20:50
 */
public class SocketContainerMgr {

//    private static Queue<Channel> channelQueue = new ConcurrentLinkedQueue<>();
    private static Map<String,Channel> channelMap = new ConcurrentHashMap<>();
    private static Map<String,Channel> channelHisMap = new ConcurrentHashMap<>();

    public static void offerChannel(SocketChannel socketChannel){
        Channel channel = new Channel();
        channel.setSocketChannel(socketChannel);
        channel.setCreateTime(new Date().getTime());
        channelMap.put(socketChannel.toString(),channel);
//        channelQueue.offer(channel);
    }
    public static void offerKey(SelectionKey selectionKey){
        if(selectionKey.channel() instanceof  SocketChannel){
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            Channel channel = channelMap.get(socketChannel.toString());
            if(channel == null){
                return;
            }
            Key key = new Key();
            key.setChannel(channel);
            key.setSelectionKey(selectionKey);
            key.setCreateTime(new Date().getTime());
            channel.putKey(selectionKey.toString(), key);
        }

    }


    public  static void closeChannel(SocketChannel socketChannel){
        if(socketChannel != null){
            try {
                socketChannel.close();
            } catch (IOException e) {
            }
        }
    }

    public  static void cancelKey(SelectionKey selectionKey){
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        Channel channel = channelMap.get(socketChannel.toString());
        if(channel == null){
            return;
        }
        channel.getKeyMap().remove(selectionKey.toString());
        if(selectionKey != null){
            selectionKey.cancel();
        }
    }

    public synchronized static void clear(){
        Set<Map.Entry<String,Channel>> sets = channelMap.entrySet();
        for(Map.Entry<String,Channel> entry : sets){
            String key = entry.getKey();
            Channel channel  = entry.getValue();
            SocketChannel socketChannel  = channel.getSocketChannel();

            if(!socketChannel.isOpen()){
                channelMap.remove(key);
                channel.setSocketResult(socketChannel.toString());
                channel.setSocketChannel(null);
                channelHisMap.put(key,channel);
            }else{
//                long startTime = channel.getCreateTime();
//                long endTime = System.currentTimeMillis();
//                long stepTime = endTime - startTime;
//                if(stepTime > 1000 * 60){
//                    //cancel key first
//                    Set<Map.Entry<String,Key>> keySets =  channel.getKeyMap().entrySet();
//                    if(CollectionUtils.isNotEmpty(keySets)){
//                        for(Map.Entry<String,Key> entryKey : keySets){
////                            if(entryKey.getValue().getSelectionKey().isReadable()){
////
////                            }
//                            cancelKey(entryKey.getValue().getSelectionKey());
//                        }
//                    }
//                    //close channel second
//                    closeChannel(channel.getSocketChannel()); //服务主动关闭
//                }
                System.out.println("a live socket="+socketChannel.toString());
            }
        }
    }

    public static  Map<String,Channel> getChannelMap(){
        return channelMap;
    }
    public static  Map<String,Channel> getHisChannelMap(){
        return channelHisMap;
    }





}
