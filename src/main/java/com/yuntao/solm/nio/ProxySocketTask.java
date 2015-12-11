package com.yuntao.solm.nio;

import com.usefullc.solm.common.proxy.nio.container.TaskModel;
import com.usefullc.solm.common.proxy.nio.contant.ProxyConstant;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * 为代理设置的task
 * Created by shengshan.tang on 2015/12/11 at 18:51
 */
public class ProxySocketTask extends   ProxyTask {

    Socket socket;

    public ProxySocketTask(TaskModel taskModel) {
        super(taskModel);
    }

    @Override
    public void build() throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("202.119.199.147",
                1080));
        socket = new Socket(proxy);
        socket.connect(new InetSocketAddress(taskModel.getReqHost(), taskModel.getReqPort()));
    }

    @Override
    public void rebuild() throws IOException {
        //build();
    }

    @Override
    public void execute() throws IOException, InterruptedException {
        if(!hasBuild){
            build();
        }
        exeCount++;
        //执行前重置参数
        result = "pause";
        success = true;
        //write req
        OutputStream os = null;
        InputStream  is = null;

        try{
            os = socket.getOutputStream();
            os.write(taskModel.getReqBuffer().array());
            os.flush();

            //read res
            is = socket.getInputStream();
            byte [] buffer = new byte[ProxyConstant.READ_BYTE_SIZE];
            int n = is.read(buffer);
            while(n != -1){
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer,0,n);
                taskModel.getSourceChannel().write(byteBuffer);
            }
            result = "finished";
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);

            SocketContainerMgr.closeChannel( getSourceChannel());
            IOUtils.closeQuietly(socket);
        }

    }

    @Override
    public void run() {
        try{
            execute();
        }catch (Exception e){
            e.printStackTrace();
            exeCount = 3;  //强制重连
            success = false;
        }
    }
}
