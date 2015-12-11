package com.yuntao.solm.nio.handler;

import com.usefullc.solm.common.proxy.nio.NioDataConfig;
import com.usefullc.solm.common.proxy.nio.SocketContainerMgr;
import com.usefullc.solm.common.proxy.nio.contant.ProxyConstant;
import com.usefullc.solm.common.proxy.nio.parse.ReqParse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by shengshan.tang on 2015/12/9 at 11:56
 */
public class ReadReqTask extends  StreamTask {

    private SelectionKey selectionKey;

    private SocketChannel channel;
    //up set from out



    public ReadReqTask(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        this.channel = (SocketChannel) selectionKey.channel();
    }



    public void read() throws IOException {
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        buffer.clear();  //清空，准备写入数据

        ReqParse reqParse = NioDataConfig.getReqParse(selectionKey.toString());

        int n = channel.read(buffer);  //读取浏览器请求的数据
        //biz get contentLen
        reqParse.parse(n, buffer);

        if(reqParse.getLoadedLen() == 0){  //TODO 还没有推送数据，后续做成event触发
            return;
        }
        //end
        if (!reqParse.isParseState()) {  //no http break
            SocketContainerMgr.closeChannel((SocketChannel) selectionKey.channel());
            result = "finished";
        }
        if(StringUtils.isEmpty(url)){
            url = reqParse.getUrl();
        }
        if (n > 0) {
            System.out.println("continue browser read client req url=" + url);
            if (reqParse.getLoadedLen() >= buffer.capacity()) {
                buffer = ByteBuffer.allocate(ProxyConstant.READ_BYTE_SIZE + buffer.capacity());
            }
        } else if (n == 0) {  //或许是0，但是读取还没有结束
            if (reqParse.isReadEnd()) {
                System.out.println("browser read finish 0,url=" + url+",key="+selectionKey);
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                result = "finished";
            }
        } else if (n == -1) {
            System.out.println("browser read finish -1,url=" + url);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
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
        }finally {
            if(result.equals("finished")){  //读结束
                SocketContainerMgr.cancelKey(selectionKey);
            }
            if(!success){  //不成功
                SocketContainerMgr.closeChannel(channel);
            }
        }
    }

    @Override
    public StreamTask buildNew() {
        StreamTask task = new ReadReqTask(selectionKey);
        return task;
    }
}
