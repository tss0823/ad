package com.yuntao.solm.service.impl;

import com.yuntao.solm.service.AdClickService;
import com.yuntao.solm.service.vo.AdParam;
import com.yuntao.solm.utils.SeleniumUtils;
import com.yuntao.solm.service.vo.AdType;
import com.thoughtworks.selenium.DefaultSelenium;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;

/**
 * 嵌入Ad
 * Created by shengshan.tang on 2015/11/22 at 18:35
 */
public class AdQrClickServiceImpl implements AdClickService {

    Logger log = org.slf4j.LoggerFactory.getLogger(AdQrClickServiceImpl.class);


    @Override
    public void click(AdParam adParam) {

        String url = adParam.getUrl();
        AdType adType = adParam.getAdType();
        long pageOpenWaitTime = adParam.getPageOpenWaitTime();
        DefaultSelenium selenium = null;
        //执行请等待
        try {
            long waitTime = Math.round(pageOpenWaitTime * 2.3);
            log.info("task run before wait for "+waitTime);
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            selenium = SeleniumUtils.getSelenium(url);
            selenium.open(url);
            String y = RandomStringUtils.random(1, "123");
            String x = RandomStringUtils.random(1,"123456789");
            selenium.click("xpath=//div[@class='container']/div["+y+"]/a["+x+"]");  //点击弹出ad
            log.info("is opend haosou，click at y=" + y + ",x=" + x);
            try {
                long waitTime = Math.round(pageOpenWaitTime * 1.3);
                log.info("after opened first ad page haosou wait for "+waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        selenium.waitForPageToLoad("8000");   //开启抛出异常，网上说是有bug
            selenium.selectPopUp(selenium.getAllWindowTitles()[1]);  //选择弹出窗口
            log.info("selected keyword is " + selenium.getTitle());
            String itemIndex = RandomStringUtils.random(1,"12345");
            try {
                selenium.click("xpath=//div[@id='m-spread-left']/ul[1]/li["+itemIndex+"]/h3[1]/a[1]");  //点击hasou item
            } catch (Exception e) {
                log.error("first click failed");
                try{
                    selenium.click("xpath=//div[@id='main']/ul[1]/li["+itemIndex+"]/h3[1]/a[1]");  //点击hasou item
                }catch (Exception e2){
                    itemIndex = "1";
                    selenium.click("xpath=//div[@id='m-spread-left']/ul[1]/li["+itemIndex+"]/h3[1]/a[1]");  //点击hasou item
                }
            }
            log.info("ad page is opened,itemIndex="+itemIndex);

            try {
                long waitTime = Math.round(pageOpenWaitTime * 1.5);
                log.info("after opened second ad page haosou wait for "+waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("run task failed,url="+url+",adType="+adType,e);
        } finally {
            SeleniumUtils.closeSelenium(selenium);
        }
    }
}
