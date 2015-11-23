package com.yuntao.solm.utils;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Created by shengshan.tang on 2015/11/22 at 21:07
 */
public class SeleniumUtils {

    public static DefaultSelenium getSelenium(String url){
        String host = "localhost";
        int port = 4444;
        String browserType = "*firefox";
        DefaultSelenium selenium = null;
        try {
            selenium = new DefaultSelenium(host, port, browserType, url);
            selenium.start();
        }catch (Exception e){
            throw new  RuntimeException(e);
        }
        return selenium;
    }

    public static void closeSelenium(DefaultSelenium selenium){
        if(selenium != null){
            selenium.stop();
        }
    }
}
