package com.yuntao.solm.service.vo;

import java.io.Serializable;

/**
 * Created by shengshan.tang on 2015/11/22 at 18:38
 */
public class AdVo  implements Serializable,Cloneable {

    private AdType adType;

    private String url;

    private int clickWeight = 5;  //1-10，点击权重

    private int waitTimeWeight = 5;  //1-10，等待时间权重

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public AdType getAdType() {
        return adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getClickWeight() {
        return clickWeight;
    }

    public void setClickWeight(int clickWeight) {
        this.clickWeight = clickWeight;
    }

    public int getWaitTimeWeight() {
        return waitTimeWeight;
    }

    public void setWaitTimeWeight(int waitTimeWeight) {
        this.waitTimeWeight = waitTimeWeight;
    }
}
