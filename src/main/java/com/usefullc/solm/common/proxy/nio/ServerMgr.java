package com.usefullc.solm.common.proxy.nio;

import com.usefullc.solm.common.proxy.nio.parse.ReqParse;
import com.usefullc.solm.common.proxy.nio.statics.ProxyStaticConnectorHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by shengshan.tang on 2015/12/8 at 13:49
 */
public class ServerMgr {

    private static int port = 8806;

    private static ExecutorService ec = null;

    private static Selector selector = null;

    public static int getPort() {
        return port;
    }

    public static void init(int paramPort) throws IOException {
        port = paramPort;
        ec = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()) {

            @Override
            protected void terminated() {
                super.terminated();
                //task close
                System.out.println("all task finished!");
            }

            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                ProxyConnectorHandler task = (ProxyConnectorHandler) r;
                System.out.println("task add to proxy finish! take time=" + (System.currentTimeMillis() - task.getStartTime()) + ",success=" + task.isSuccess()+",url=" + task.getUrl() );

            }
        };
        //end

        selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);// 设置为非阻塞方式
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.register(selector, SelectionKey.OP_ACCEPT);// 注册监听的事件
    }

    public static void startServer() {
        //设置代理
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SelectionKey key = null;
                int readByteSize = 1024;
                try {
                    if (selector.select(20) <= 0) {
                        return;
                    }
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();// 取得所有key集合
                    Iterator<SelectionKey> it = selectedKeys.iterator();
                    while (it.hasNext()) {
                        key = it.next();
                        if (!key.isValid()) {
                            continue;
                        }
                        SocketContainerMgr.offerKey(key);
                        //TODO 每次push，虽然 read key 一样
                        NioDataConfig.initReqParse(key);
                        it.remove();
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                            SocketChannel sc = ssChannel.accept();// 接受到服务端的请求
                            sc.configureBlocking(false);
                            ByteBuffer buffer = ByteBuffer.allocate(readByteSize);  //准备1024个字节数据放到attachment中。
                            sc.register(selector, SelectionKey.OP_READ, buffer);
                            SocketContainerMgr.offerChannel(sc);  //offer socketChannel

                        } else if (key.isReadable()) {
                            //read
//                            ReadHandler.readReq(key);
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            buffer.clear();  //清空，准备写入数据

                            ReqParse reqParse = NioDataConfig.getReqParse(key.toString());
                            while (true) {
                                int n = sc.read(buffer);  //读取浏览器请求的数据
                                //biz get contentLen
                                reqParse.parse(n, buffer);
                                //end
                                if(!reqParse.isParseState()){  //no http break
                                    SocketContainerMgr.closeChannel((SocketChannel) key.channel());
                                    break;
                                }

                                if (n > 0) {
                                    System.out.println("continue read client req n=" + n);
                                    if (reqParse.getLoadedLen() >= buffer.capacity()) {
                                        buffer = ByteBuffer.allocate(readByteSize + buffer.capacity());
                                    }
                                    continue;
                                } else if (n == 0) {  //或许是0，但是读取还没有结束
                                    if (reqParse.isReadEnd()) {
                                        System.out.println("browser close connect 0,client req url=" + reqParse.getUrl());
                                        key.interestOps(SelectionKey.OP_WRITE);
                                        break;
                                    }
                                } else if (n == -1) {
                                    System.out.println("browser close connect -1,client req url=" + reqParse.getUrl());
                                    key.interestOps(SelectionKey.OP_WRITE);
                                    break;
                                }

                                //buffer.flip();
//                            System.out.println(new String(buffer.array(), 0, n));
                            }

                        } else if (key.isWritable()) {
//                        SocketChannel sourceChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();  //获取附件数据
                            buffer.flip();  //反转，准备读取buffer
                            if (buffer.limit() == 0) {  //没有数据
                                System.err.println(0 + " " + NioDataConfig.getReqParse(key.toString()).getUrl());
                                SocketContainerMgr.closeChannel((SocketChannel) key.channel());
//                            key.cancel();
//                            key.channel().close();
                            } else {
                                NioDataConfig.clearReqParse(key.toString());
                                //System.err.println(new String(buffer.array(), 0, buffer.limit()));
//                        clientRequest(buffer, key);   //之后这个抽出一个线程出来,目前对于慢的req卡死这这里 TODO
                                ec.execute(new ProxyStaticConnectorHandler(buffer, key));

                                SocketContainerMgr.cancelKey(key);
//                            key.cancel();  //及时取消，保证不被堵塞
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String url = null;
                    if (key != null) {
                        url = NioDataConfig.getReqParse(key.toString()).getUrl();
                    }
//                    System.err.println("restart server,url=" + url);
//                    startServer();   //报错，则重启
                }
            }
        }, 0, 10, TimeUnit.MILLISECONDS);


    }
}
