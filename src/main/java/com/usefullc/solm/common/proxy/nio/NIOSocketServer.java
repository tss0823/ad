/*
 * Copyright 2010-2011 ESunny.com All right reserved. This software is the confidential and proprietary information of
 * ESunny.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with ESunny.com.
 */
package com.usefullc.solm.common.proxy.nio;

import com.usefullc.solm.common.proxy.nio.handler.ReadHandler;
import com.usefullc.solm.common.proxy.nio.parse.ReqParse;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 * 类SocketServer.java的实现描述：TODO 类实现描述
 *
 * @author shengshang.tang 2014年12月20日 下午3:20:32
 */
public class NIOSocketServer {



    public static void main(String[] args) {
        try {
            //创建proxy connect pool
            System.setProperty("htmlDir",args[0]);
            int port = Integer.valueOf(args[1]);
            ServerMgr.init(port);
            //end

            System.out.println("htmlDir="+args[0]);
            System.out.println("port="+args[1]);

            ProxyTaskExecutor.start();  //代理线程开始

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerMgr.startServer();

//                    ReadHandler.start();  //读取browser req and server res 启动
                }
            }).start();

            System.out.println("server is start at "+ServerMgr.getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
