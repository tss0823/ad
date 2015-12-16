package com.usefullc.solm.common.proxy.nio.statics;

import com.usefullc.solm.common.proxy.nio.ProxyTask;
import com.usefullc.solm.common.proxy.nio.SocketContainerMgr;
import com.usefullc.solm.common.proxy.nio.container.TaskModel;
import com.usefullc.solm.common.proxy.nio.contant.ProxyConstant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * 为代理设置的task
 * Created by shengshan.tang on 2015/12/11 at 18:51
 */
public class ProxySaticSocketTask extends ProxyTask {

    File file;

    String extension;

    String contentType;


    public ProxySaticSocketTask(TaskModel taskModel) {
        super(taskModel);
    }

    @Override
    public void build() throws IOException {
        String fullUrl = "http://"+getTaskModel().getReqHost();
        if(getTaskModel().getReqPort() != 80){
            fullUrl += ":"+getTaskModel().getReqPort();
        }
        fullUrl += "/"+getTaskModel().getUrl();
        URL aURL = new URL(fullUrl);
        String path = aURL.getPath();
        extension  = FilenameUtils.getExtension(path);

        if(StringUtils.endsWith(path,"jpg") || StringUtils.endsWith(path, "jpeg") ||
                StringUtils.endsWith(path, "png") || StringUtils.endsWith(path, "gif")){
            contentType = "image/"+extension;
        }else if(StringUtils.endsWith(path,"ico")){
            contentType = "image/x-icon";
        }else if(StringUtils.endsWith(path,"css")){
            contentType = "text/css";
        }else if(StringUtils.endsWith(path,"js")){
            contentType = "application/javascript";
        }else if(StringUtils.endsWith(path,"htm") || StringUtils.endsWith(path,"html") ||
                StringUtils.endsWith(path,"php")){
            contentType = "text/html";
        }else{
            contentType = "text/html";
            extension = "html";
            path += "index.html";
        }
        String query = aURL.getQuery();
        if(StringUtils.isNotEmpty(query)){
            path += query.replaceFirst("\\?","");
        }
        String htmlDir = System.getProperty("htmlDir");
        String filePath = htmlDir + File.separator + path;
        file = FileUtils.getFile(filePath);

    }


    @Override
    public void rebuild() throws IOException {
        //build();
    }

    @Override
    public void execute() throws IOException, InterruptedException {
        if (!hasBuild) {
            build();
        }
        exeCount++;
        //执行前重置参数
        result = "pause";
        success = true;
        //write req
        try {
            //get file byte
            byte fileByte [] = FileUtils.readFileToByteArray(file);

            Integer contentLen = fileByte.length;

            //get header str
            String headers = getHeaders(contentLen.longValue(),contentType);
            byte [] headerByte = headers.getBytes();

            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(headerByte.length+fileByte.length);
            byteArrayBuffer.append(headerByte,0,headerByte.length);
            byteArrayBuffer.append(fileByte,0,fileByte.length);

            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayBuffer.buffer());
            taskModel.getSourceChannel().write(byteBuffer);
            result = "finished";
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SocketContainerMgr.closeChannel(getSourceChannel());
        }

    }
   private String getHeaders(Long contentLen,String contentType) {
       StringBuilder sb = new StringBuilder();
       sb.append("HTTP/1.1 200 OK\r\n");
       sb.append("Date: "+new Date().toString()+"\r\n");
       sb.append("Connection: close\r\n");
       sb.append("Content-Length: "+contentLen+"\r\n");
       sb.append("Content-Type: "+contentType+"\r\n");
       sb.append("Server: haha\r\n");
//        headers.put("Last-Modified","");
//        headers.put("ETag","");
//        headers.put("Accept-Ranges","");
       sb.append("\r\n");
       return sb.toString();


    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            exeCount = 3;  //强制重连
            success = false;
        }
    }
}
