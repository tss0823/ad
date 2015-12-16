package com.yuntao.solm.demo;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.*;

public class WebDriverFirefox2 {
    public static void main(String[] args) {

        WebDriver driver = new FirefoxDriver();



        //Launch website

        driver.navigate().to("http://test.bos.yuntaohongbao.com/login.html?test=aabbcc");
        Cookie name = new Cookie("mycookie", "123");
        driver.manage().addCookie(name);

        // After adding the cookie we will check that by displaying all the cookies.
        Set<Cookie> cookiesList =  driver.manage().getCookies();
        for(Cookie getcookies :cookiesList) {
            System.out.println(getcookies );
        }

        driver.navigate().refresh();


        //Maximize the browser
        driver.manage().window().maximize();

        driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/form/div[2]/div[1]/input")).sendKeys("tangshengshan");

//        driver.quit();
    }
}