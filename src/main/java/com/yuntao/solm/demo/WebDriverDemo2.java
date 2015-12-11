package com.yuntao.solm.demo;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.*;
import java.util.concurrent.*;

public class WebDriverDemo2 {
    public static void main(String[] args) {
//        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");

        String proxyIp = "127.0.0.1";
        int proxyPort = 8806;
        FirefoxProfile profile = new FirefoxProfile();
        // 使用代理
        profile.setPreference("network.proxy.type", 1);
        // http协议代理配置
        profile.setPreference("network.proxy.http", proxyIp);
        profile.setPreference("network.proxy.http_port", proxyPort);

//        profile.setPreference("network.proxy.ssl", proxyIp);
//        profile.setPreference("network.proxy.ssl_port", proxyPort);

        // 所有协议公用一种代理配置，如果单独配置，这项设置为false
        profile.setPreference("network.proxy.share_proxy_settings", true);

        // 对于localhost的不用代理，这里必须要配置，否则无法和webdriver通讯
        profile.setPreference("network.proxy.no_proxies_on", "localhost");



        WebDriver driver = new FirefoxDriver(profile);


//        Proxy proxy = new Proxy();

        //Launch website
        driver.navigate().to("http://test.bos.yuntaohongbao.com/login.html?test=abc1");

        //Maximize the browser
        driver.manage().window().maximize();

        driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/form/div[2]/div[1]/input")).sendKeys("tangshengshan");

        driver.quit();
    }
}