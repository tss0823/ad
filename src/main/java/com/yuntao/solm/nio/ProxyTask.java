package com.yuntao.solm.nio;

import com.usefullc.solm.common.proxy.nio.container.Channel;
import com.usefullc.solm.common.proxy.nio.container.TaskModel;
import com.usefullc.solm.common.proxy.nio.handler.ReadHandler;
import com.usefullc.solm.common.proxy.nio.handler.ReadResTask;
import com.usefullc.solm.common.proxy.nio.parse.ResParse;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by shengshan.tang on 2015/12/2 at 22:27
 */
public class ProxyTask implements Runnable {

    //get from outer
    protected TaskModel taskModel;
    //end

    SocketChannel agentChannel;

    Selector selector;

    protected boolean success = true;

    protected String result = "pause";

    protected boolean hasBuild;

    protected int rebuildCount;

    protected int exeCount;  //执行次数



    public ProxyTask(TaskModel taskModel) {
        this.taskModel = taskModel;
    }



    public String getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUrl() {
        return taskModel.getUrl();
    }

    public Long getStartTime() {
        return taskModel.getStartTime();
    }

    public int getRebuildCount() {
        return rebuildCount;
    }

    public SocketChannel getAgentChannel() {
        return agentChannel;
    }

    public SocketChannel getSourceChannel() {
        return taskModel.getSourceChannel();
    }

    public int getExeCount() {
        return exeCount;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    public void build() throws IOException {
        //构建代理请求数据
        taskModel.setStartBuildTime(new Date().getTime());

        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("58.251.47.101", 8081));
        Socket socket = new Socket(proxy);
        socket.setReuseAddress(true);
        SocketAddress address = new InetSocketAddress(taskModel.getReqHost(), taskModel.getReqPort());
        agentChannel = socket.getChannel().open();
        agentChannel.configureBlocking(false);
        agentChannel.connect(address);
        selector = Selector.open();
        agentChannel.register(selector, SelectionKey.OP_CONNECT);

        SocketContainerMgr.offerChannel(agentChannel);  //add

        //set biz channel
        Channel bizSourceChannel = SocketContainerMgr.getChannelMap().get(getSourceChannel().toString());
        bizSourceChannel.setBindTask(true);
        bizSourceChannel.setUrl(taskModel.getUrl());

        Channel bizAgentChannel = SocketContainerMgr.getChannelMap().get(agentChannel.toString());
        bizAgentChannel.setBindTask(true);
        bizAgentChannel.setUrl(taskModel.getUrl());
        bizAgentChannel.setAgent(true);
        hasBuild = true;
        //end
    }

    public void rebuild() throws IOException {
        //shudown agentChannel
        SocketContainerMgr.closeChannel(agentChannel);
        System.out.println("rebuild task url="+getUrl());
        build();
        rebuildCount++;
    }


    public void execute() throws IOException, InterruptedException {
        if(!hasBuild){
            build();
        }
        exeCount++;
        //执行前重置参数
        result = "pause";
        success = true;
        Iterator<SelectionKey> iter = null;
        if (selector.select(1000) <= 0) {
           return;
        }
        iter = selector.selectedKeys().iterator();
        while (iter.hasNext()) {  //很多数据 selectKey,(可能由一个request切割成多个,key 第一次可能是write,也有可能是read)
            SelectionKey key = iter.next();
            iter.remove(); // 防止重复利用
            if (!key.isValid()) {
                continue;
            }
            if(getUrl().startsWith("http://e.tf.360.cn/search/rec?t=")){
                System.out.println("stop");
            }
            SocketContainerMgr.offerKey(key);

            // SocketChannel sourceChannel = (SocketChannel) key.channel();
            if (key.isConnectable() && agentChannel.finishConnect()) {
//                System.out.println("req start url=" + getUrl());
                agentChannel.write(taskModel.getReqBuffer());
                key.interestOps(SelectionKey.OP_READ);

                //read
            } else if (key.isReadable()) {
                //read
//                ReadHandler.readRes(key, getSourceChannel(),getUrl());
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                ResParse resParse = new ResParse(key);

                while (true) {
                    buffer.clear();
                    int n = agentChannel.read(buffer);
                    //biz get contentLen
                    resParse.parse(n, buffer);
                    //end
                    if(!resParse.isParseState()){  //not http break
                        result = "finished";
//                        SocketContainerMgr.closeChannel(channel);
                        break;
                    }

                    buffer.flip();
                    taskModel.getSourceChannel().write(buffer);  //写入到 source socket

                    if (n > 0) {
                        System.out.println("agent continue read ,url="+getUrl());
                        continue;
                    } else if (n == 0) {  //或许是0，但是读取还没有结束
                        if (resParse.isReadEnd()) {
                            System.out.println("agent close connect 0,url=" + getUrl());

//                            key.cancel();
//                            key.channel().close();
//                            url = reqParse.getUrl();
                            result = "finished";
                            break;
                        }
                    } else if (n == -1) {
                        System.out.println("agent close connect -1,url=" + getUrl());
//                        SocketContainerMgr.closeChannel(channel);
//                        key.cancel();
//                        key.channel().close();
//                        url = reqParse.getUrl();
                        result = "finished";
                        break;
                    }

                }

                //write
            }
//            else if (key.isWritable()) {
//                //buffer.flip();
//                agentChannel.write(taskModel.getReqBuffer());  //写入buffer
//                key.interestOps(SelectionKey.OP_READ);
//
//            }

        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }finally {
            if(result.equals("finished") || !success){  //复制完毕，和不成功
                SocketContainerMgr.closeChannel( getSourceChannel());
                SocketContainerMgr.closeChannel(agentChannel);
            }
        }
    }
}
