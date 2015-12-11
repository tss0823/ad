package com.yuntao.solm.demo;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Created by shengshan.tang on 2015/11/20 at 18:38
 */
public class Solm2Test {

    public static void main(String[] args) {



    }


    public static void runTask() {
        String host = "localhost";
        int port = 4444;
        String url = "http://www.ccdidi.com/index.html";
        String browserType = "*firefox";
        DefaultSelenium selenium = null;
        try {
            selenium = new DefaultSelenium(host, port, browserType, url);
            selenium.start();
            selenium.open(url);
//        String source = selenium.getHtmlSource();
//        System.out.println(source);
//        selenium.
            selenium.click("xpath=//div[@class='container']/div[1]/a[1]");  //点击弹出ad
            System.out.println("一打开haosou");
            long startTime = System.currentTimeMillis();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        selenium.waitForPageToLoad("8000");   //开启抛出异常，网上说是有bug
            System.out.println("take time=" + (System.currentTimeMillis() - startTime));
            selenium.selectPopUp(selenium.getAllWindowTitles()[1]);  //选择弹出窗口
            System.out.println(selenium.getTitle());
            try {
                selenium.click("xpath=//div[@id='m-spread-left']/ul[1]/li[1]/h3[1]/a[1]");  //点击hasou item
            } catch (Exception e) {
                System.out.println("first click failed");
                selenium.click("xpath=//div[@id='main']/ul[1]/li[1]/h3[1]/a[1]");  //点击hasou item
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(selenium != null){
                selenium.stop();
            }
        }


    }
}
