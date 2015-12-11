package com.usefullc.solm.common.proxy.nio.container;

import java.io.Serializable;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by shengshan.tang on 2015/12/7 at 20:53
 */
public class Channel implements Serializable {

    private Map<String,Key> keyMap = new ConcurrentHashMap<>();

    private SocketChannel socketChannel;

    private long createTime;

    private String url;

    private boolean bindTask;

    private boolean agent;  //是否代理

    private String socketResult;


    public Map<String,Key> getKeyMap() {
        return keyMap;
    }


    public void putKey(String strKey,Key key) {
        this.keyMap.put(strKey,key);
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBindTask() {
        return bindTask;
    }

    public void setBindTask(boolean bindTask) {
        this.bindTask = bindTask;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public String getSocketResult() {
        return socketResult;
    }

    public void setSocketResult(String socketResult) {
        this.socketResult = socketResult;
    }
}
