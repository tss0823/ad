package com.usefullc.solm.common.proxy.nio.container;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by shengshan.tang on 2015/12/9 at 10:10
 */
public class TaskModel implements Serializable {

    Long startTime;

    String url;

    String reqHost;

    int reqPort;

    ByteBuffer reqBuffer;

    SocketChannel sourceChannel;
    //以上是必须参数

    long startBuildTime;

    long startExecTime;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReqHost() {
        return reqHost;
    }

    public void setReqHost(String reqHost) {
        this.reqHost = reqHost;
    }

    public int getReqPort() {
        return reqPort;
    }

    public void setReqPort(int reqPort) {
        this.reqPort = reqPort;
    }

    public ByteBuffer getReqBuffer() {
        return reqBuffer;
    }

    public void setReqBuffer(ByteBuffer reqBuffer) {
        this.reqBuffer = reqBuffer;
    }

    public SocketChannel getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(SocketChannel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public long getStartBuildTime() {
        return startBuildTime;
    }

    public void setStartBuildTime(long startBuildTime) {
        this.startBuildTime = startBuildTime;
    }

    public long getStartExecTime() {
        return startExecTime;
    }

    public void setStartExecTime(long startExecTime) {
        this.startExecTime = startExecTime;
    }
}
