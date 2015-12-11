package com.usefullc.solm.common.proxy.nio.handler;

import com.usefullc.solm.common.proxy.nio.NioDataConfig;
import com.usefullc.solm.common.proxy.nio.ProxyTask;
import com.usefullc.solm.common.proxy.nio.SocketContainerMgr;
import com.usefullc.solm.common.proxy.nio.contant.ProxyConstant;
import com.usefullc.solm.common.proxy.nio.parse.ReqParse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Created by shengshan.tang on 2015/12/9 at 11:55
 */
public class ReadHandler {

    private static Lock  readReqLock = new ReentrantLock();
    private static Lock  readResLock = new ReentrantLock();

    private static ExecutorService ec;

    private static List<SelectionKey> keyList = new ArrayList<>();

    public static void start(){
        //创建 read pool
        ec = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()) {

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                StreamTask task = (StreamTask) r;
                if(!task.isSuccess()){
                    System.err.println("task read execute failed! activeTaskCount/taskCount="+this.getActiveCount()+"/"+this.getPoolSize()+",url="+task.getUrl());
                    return;
                }
                if(task.getResult().equals("pause")){
                    System.out.println("task read execute pause! activeTaskCount/taskCount="+this.getActiveCount()+"/"+this.getPoolSize()+"|"+this.getQueue().size()+",url="+task.getUrl());
//                    pauseLbQueue.offer(task);
//                    this.execute(task);  //添加再执行
                    this.execute(task.buildNew());
                    return;
                }
                System.out.println("task read execute success! activeTaskCount/taskCount="+this.getActiveCount()+"/"+this.getPoolSize()+",url="+task.getUrl());

            }
        };
        //end
    }

    private static void addTask(StreamTask task){
        ec.execute(task);
    }

    public static void readReq(SelectionKey selectionKey){
        //event 触发read TODO
        readReqLock.lock();
        try{
            if(!keyList.contains(selectionKey)){
                addTask(new ReadReqTask(selectionKey));
                keyList.add(selectionKey);
            }
        }catch (Exception e){
        }finally {
            readReqLock.unlock();
        }
    }
    public static void readRes(SelectionKey selectionKey,SocketChannel browserChannel, String url){
        //event 触发read TODO
        readResLock.lock();
        try{
            if(!keyList.contains(selectionKey)){
                addTask(new ReadResTask(selectionKey,browserChannel,url));
                keyList.add(selectionKey);
            }
        }catch (Exception e){
        }finally {
            readResLock.unlock();
        }
    }

    public static void readReqTmp(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();


    }

}
