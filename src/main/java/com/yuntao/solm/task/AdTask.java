package com.yuntao.solm.task;

import com.yuntao.solm.service.AdClickService;
import com.yuntao.solm.service.ServiceFacotry;
import com.yuntao.solm.service.vo.AdParam;
import com.yuntao.solm.service.vo.AdType;
import org.slf4j.Logger;

/**
 * Created by shengshan.tang on 2015/11/22 at 19:31
 */
public class AdTask implements  Runnable {

    Logger log = org.slf4j.LoggerFactory.getLogger(AdTask.class);

    AdClickService adQrClickService = (AdClickService) ServiceFacotry.getBean("adQrClickService");
    AdClickService adXfClickService = (AdClickService) ServiceFacotry.getBean("adXfClickService");

    private String id;

    private AdType adType;

    private String url;

    private long openPageWaitTime = 5000;



    @Override
    public void run() {
        log.info("run start,url="+url+",adType="+adType);
        AdParam adParam = new AdParam();
        adParam.setAdType(adType);
        adParam.setUrl(url);
        adParam.setPageOpenWaitTime(openPageWaitTime);
        if(adType.equals(AdType.QR)){
            adQrClickService.click(adParam);
        }else{
            adXfClickService.click(adParam);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
