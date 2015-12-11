package com.usefullc.solm.common.proxy.nio.handler;

/**
 * Created by shengshan.tang on 2015/12/9 at 12:27
 */
public abstract class StreamTask implements Runnable {

    protected boolean success = true;

    protected String result = "pause";

    protected  String url;

    protected int exeCount;

    public boolean isSuccess() {
        return success;
    }

    public String getResult() {
        return result;
    }

    public int getExeCount() {
        return exeCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public abstract StreamTask buildNew();
}
