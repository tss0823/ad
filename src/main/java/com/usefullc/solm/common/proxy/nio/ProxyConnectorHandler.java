package com.usefullc.solm.common.proxy.nio;

import com.usefullc.solm.common.proxy.nio.container.Channel;
import com.usefullc.solm.common.proxy.nio.container.TaskModel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by shengshan.tang on 2015/12/2 at 11:00
 */
public class ProxyConnectorHandler implements Runnable {

    private boolean success;
    private Long startTime;
    private String url;
    private String reqHost;
    private int reqPort;

    private ByteBuffer reqBuffer;

    private SelectionKey reqKey;


    public boolean isSuccess() {
        return success;
    }

    public Long getStartTime() {
        return startTime;
    }

    public String getUrl() {
        return url;
    }

    public ProxyConnectorHandler(ByteBuffer buffer, SelectionKey key) {
        this.reqBuffer = buffer;
        this.reqKey = key;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        try {
            //解析buffer  TODO 之后放到 reqParse 中去做
            reqPort = 80;
            //获取host 和 port
            String reqStr = new String(reqBuffer.array(), 0, reqBuffer.limit());
            int start = reqStr.indexOf("CONNECT ");
//                    int start = curReqStr.indexOf("Host: ");
            int end = 0;
            if (start == -1) {
                start = reqStr.indexOf("Host: ");
                start += 6;
                end = reqStr.indexOf("\r\n", start);
            } else {
                start += 8;
                end = reqStr.indexOf(" ", start);
            }
            try {
                reqHost = reqStr.substring(start, end).trim();
            } catch (Exception e) {
                System.out.println(e);
            }

            //
            if(StringUtils.isEmpty(reqHost)){
                System.err.println("reqHost is nul \n"+reqStr);
                return;
            }

            if (reqHost.indexOf(":") != -1) {
                String strs[] = reqHost.split(":");
                reqHost = strs[0];
                reqPort = Integer.valueOf(strs[1]);
            }
            //end
            if (reqPort == 443) {
                System.out.println("skip 443 host=" + reqHost);
                return;
            }

            //get req url
            start = reqStr.indexOf("GET ");
            if (start == -1) {
                start = reqStr.indexOf("POST ");
                if(start == -1){
                    System.out.println("start not found");
                    System.out.println(reqStr);
                    return;
                }
                start+=5;
            }else{
                start += 4;
            }

            end = reqStr.indexOf(" ", start);
            url = reqStr.substring(start, end);
//            if(!url.endsWith(".css")){
//                return;
//            }

            //添加任务
            TaskModel taskModel = new TaskModel();
            taskModel.setUrl(url);
            taskModel.setReqBuffer(reqBuffer);
            taskModel.setReqHost(reqHost);
            taskModel.setReqPort(reqPort);
            taskModel.setStartTime(startTime);
            taskModel.setSourceChannel((SocketChannel) reqKey.channel());
            String doubleProxy = System.getProperty("doubleProxy");
            ProxyTask task = null;
            if(StringUtils.equalsIgnoreCase(doubleProxy,"true")){  //双重代理服务器
                task = new ProxySocketTask(taskModel);
            }else{
                task = new ProxyTask(taskModel);
            }
            ProxyTaskExecutor.addTask(task);
            success = true;

        } catch (Exception e) {
            e.printStackTrace();
            success = false;

        }finally {
            if(!success){  //添加任务失败，提前关闭
                SocketContainerMgr.closeChannel((SocketChannel) reqKey.channel());
            }
        }

    }
}
