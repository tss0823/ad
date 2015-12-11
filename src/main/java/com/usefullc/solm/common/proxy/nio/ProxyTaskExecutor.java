package com.usefullc.solm.common.proxy.nio;

import com.usefullc.solm.common.proxy.nio.container.Channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by shengshan.tang on 2015/12/2 at 22:26
 */
public class ProxyTaskExecutor {

    private static ExecutorService ec;

    private static LinkedBlockingQueue<ProxyTask> pauseLbQueue = new LinkedBlockingQueue<>();

    public static void start(){
        //创建proxy connect pool
//        ExecutorService ec = Executors.newFixedThreadPool(5);

        ec = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()) {

//            @Override
//            protected void terminated() {
//                super.terminated();
//                //task close
//                System.out.println("all task finished!");
//            }
//
//            @Override
//            protected void beforeExecute(Thread t, Runnable r) {
//                super.beforeExecute(t, r);
//            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                ProxyTask task = (ProxyTask) r;
                if(!task.isSuccess()){
                    System.err.println("task execute failed! activeTaskCount/taskCount="+this.getActiveCount()+"/"+this.getPoolSize()+",url="+task.getUrl());
                    return;
                }
                if(task.getResult().equals("pause")){
                        System.out.println("task execute pause! activeTaskCount/taskCount="+this.getActiveCount()+"/"+this.getPoolSize()+"|"+this.getQueue().size()+",url="+task.getUrl());
                    pauseLbQueue.offer(task);
//                    this.execute(task);  //添加再执行
                    return;
                }
                System.out.println("task execute success! activeTaskCount/taskCount="+this.getActiveCount()+"/"+this.getPoolSize()+",url="+task.getUrl());

            }
        };
        //end

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    ProxyTask task = pauseLbQueue.take();
                    if (task.getAgentChannel() != null && task.getAgentChannel().isOpen()) {  //is open
                        long startTime = task.getStartTime();
                        long endTime = System.currentTimeMillis();
                        long stepTime = endTime - startTime;
                        if((stepTime >= 1000 * 15 || task.getExeCount() >= 3) && task.getRebuildCount() == 0){
                            //rebuild
                            task.rebuild();
                        }if(stepTime >= 1000 * 30){
                            //shudown TODO
                            SocketChannel agentChannel = task.getAgentChannel();
                            SocketChannel sourceChannel = task.getSourceChannel();
                            SocketContainerMgr.closeChannel(sourceChannel);
                            SocketContainerMgr.closeChannel(agentChannel);
                            System.err.println("force shutdown socket,url="+task.getUrl());
                        }else{
                            ec.execute(task);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 3, 200, TimeUnit.MILLISECONDS);


        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String,Channel> channelMap = SocketContainerMgr.getChannelMap();
                    System.out.println("channel map size="+channelMap.size());
                    Map<String,Channel> hisChannelMap = SocketContainerMgr.getHisChannelMap();
                    System.out.println("his channel map size="+hisChannelMap.size());

                    //clear
                    SocketContainerMgr.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },3,8000,TimeUnit.MILLISECONDS);


    }

    public static void addTask(ProxyTask proxyTask){
        ec.execute(proxyTask);
    }




}
