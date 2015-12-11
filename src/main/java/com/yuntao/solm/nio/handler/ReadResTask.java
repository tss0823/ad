package com.yuntao.solm.nio.handler;

import com.usefullc.solm.common.proxy.nio.SocketContainerMgr;
import com.usefullc.solm.common.proxy.nio.parse.ResParse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by shengshan.tang on 2015/12/9 at 12:28
 */
public class ReadResTask extends StreamTask {

    private SelectionKey selectionKey;

    private SocketChannel agentChannel;

    private SocketChannel browserChannel;

    //up set from out


    public ReadResTask(SelectionKey selectionKey, SocketChannel browserChannel, String url) {
        this.selectionKey = selectionKey;
        this.agentChannel = (SocketChannel) selectionKey.channel();
        this.browserChannel = browserChannel;
        this.url = url;
    }

    public void read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ResParse resParse = new ResParse(selectionKey);

        buffer.clear();
        int n = agentChannel.read(buffer);
        //biz get contentLen
        resParse.parse(n, buffer);
        //end
        if (!resParse.isParseState()) {  //not http break
            result = "finished";
        }

        if(resParse.getLoadedLen() == 0){  //TODO 还没有推送数据，后续做成event触发
            return;
        }

        buffer.flip();
        browserChannel.write(buffer);  //写入到 source socket

        if (n > 0) {
            System.out.println("agent continue read ,url=" + url);
        } else if (n == 0) {  //或许是0，但是读取还没有结束
            if (resParse.isReadEnd()) {
                System.out.println("agent close connect 0,url=" + url);
                result = "finished";
            }
        } else if (n == -1) {
            System.out.println("agent close connect -1,url=" + url);
            result = "finished";
        }


    }

    @Override
    public void run() {
        exeCount++;
        try {
            read();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (result.equals("finished")) {  //写结束
                SocketContainerMgr.cancelKey(selectionKey);
            }
            if (!success) {  //不成功
                SocketContainerMgr.closeChannel(browserChannel);
                SocketContainerMgr.closeChannel(agentChannel);
            }
        }
    }

    @Override
    public StreamTask buildNew() {
        return new ReadResTask(selectionKey,browserChannel,url);
    }
}
