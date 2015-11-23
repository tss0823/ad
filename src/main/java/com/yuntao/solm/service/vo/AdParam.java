package com.yuntao.solm.service.vo;

/**
 * Created by shengshan.tang on 2015/11/22 at 20:53
 */
public class AdParam {

    String url;

    AdType adType;

    long pageOpenWaitTime;

    public long getPageOpenWaitTime() {
        return pageOpenWaitTime;
    }

    public void setPageOpenWaitTime(long pageOpenWaitTime) {
        this.pageOpenWaitTime = pageOpenWaitTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AdType getAdType() {
        return adType;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }
}
