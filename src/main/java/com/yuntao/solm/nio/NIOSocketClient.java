/*
 * Copyright 2010-2011 ESunny.com All right reserved. This software is the confidential and proprietary information of
 * ESunny.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with ESunny.com.
 */
package com.yuntao.solm.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * 类NIOSocketClient.java的实现描述：TODO 类实现描述
 * 
 * @author shengshang.tang 2014年12月21日 下午3:00:52
 */
public class NIOSocketClient {

    public static void main(String[] args) {
        test2();
    }

    public static void test1() {
        try {
            // 建立到服务端的链接
            SocketAddress address = new InetSocketAddress("127.0.0.1", 8806);
            SocketChannel channel = SocketChannel.open(address);

            channel.configureBlocking(false);

            // 创建静态的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(255);

            // 读取数据,到buffer中
            channel.read(buffer);

            buffer.flip();

            channel.close();

            // 输出缓冲区的数据
            System.out.println(new String(buffer.array(), 0, buffer.limit()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test2() {
        try {
            // 建立到服务端的链接
            SocketAddress address = new InetSocketAddress("127.0.0.1", 8806);
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(address);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);
            boolean state = true;
            while (state) {
                if (selector.select(3000) <= 0) {
                    Thread.sleep(200);
                    continue;
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove(); // 防止重复利用
                    // SocketChannel sourceChannel = (SocketChannel) key.channel();
                    if (key.isConnectable() && channel.finishConnect()) {
                        System.out.println("connection ing...");
                        key.interestOps(SelectionKey.OP_WRITE);

                    }

                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        while (true) {
                            buffer.clear();
                            int n = channel.read(buffer);
                            if (n <= 0) {
                                break;
                            }
                            //buffer.flip();
                            System.out.println(new String(buffer.array(), 0, n));
                            state = false;
                        }

                    }

                    if(key.isWritable()) {
                        channel.write(ByteBuffer.wrap("client is start!开始了".getBytes()));
                        key.interestOps(SelectionKey.OP_READ);

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
