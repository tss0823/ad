package com.yuntao.solm.nio.container;

import java.io.Serializable;
import java.nio.channels.SelectionKey;

/**
 * Created by shengshan.tang on 2015/12/7 at 20:54
 */
public class Key  implements Serializable{

    private Channel channel;

    private SelectionKey selectionKey;

    private long createTime;

    private String url;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }
}
