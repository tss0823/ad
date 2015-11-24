package com.yuntao.solm.task;

import com.yuntao.solm.service.AdClickService;
import com.yuntao.solm.service.ServiceFacotry;
import com.yuntao.solm.service.vo.AdParam;
import com.yuntao.solm.service.vo.AdType;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by shengshan.tang on 2015/11/22 at 19:31
 */
public class AdTask implements  Runnable {

    Logger log = org.slf4j.LoggerFactory.getLogger("bis");

    AdClickService adQrClickService = (AdClickService) ServiceFacotry.getBean("adQrClickService");
    AdClickService adXfClickService = (AdClickService) ServiceFacotry.getBean("adXfClickService");
    AdClickService adWdClickService = (AdClickService) ServiceFacotry.getBean("adWdClickService");

    private int index;

    private long startTime;

    private boolean success = true;

    private AdType adType;

    private String url;

    private long openPageWaitTime = 5000;



    @Override
    public void run() {
        startTime = new Date().getTime();
        log.info("run start,url="+url+",adType="+adType);
        AdParam adParam = new AdParam();
        adParam.setAdType(adType);
        adParam.setUrl(url);
        adParam.setPageOpenWaitTime(openPageWaitTime);

        try{
            adWdClickService.click(adParam);
        }catch (Exception e){
            success = false;
            log.error(e.getMessage(),e);
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

    public long getOpenPageWaitTime() {
        return openPageWaitTime;
    }

    public void setOpenPageWaitTime(long openPageWaitTime) {
        this.openPageWaitTime = openPageWaitTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
