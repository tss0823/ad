package com.yuntao.solm.service.impl;

import com.yuntao.solm.constant.AppConstant;
import com.yuntao.solm.service.AdClickService;
import com.yuntao.solm.service.vo.AdParam;
import com.yuntao.solm.service.vo.AdType;
import com.yuntao.solm.utils.ConfigUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by shengshan.tang on 2015/11/24 at 14:27
 */
public class AdWdClickServiceImpl implements AdClickService {

    Logger log = org.slf4j.LoggerFactory.getLogger("bis");

    @Override
    public void click(AdParam adParam) {

        String url = adParam.getUrl();
        String currentUrl = url;
        String currentTitle = null;
        AdType adType = adParam.getAdType();
        long pageOpenWaitTime = adParam.getPageOpenWaitTime();
        System.setProperty("webdriver.chrome.driver", ConfigUtils.getString(AppConstant.CHROME_DRIVER_PATH));
        WebDriver driver = null;
        try {

            driver = new ChromeDriver();
            //Launch website
            try {
                long waitTime = Math.round(pageOpenWaitTime * 2.3);
                log.info("task run before wait for "+waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
            }

            driver.navigate().to(currentUrl);

            //Maximize the browser
            driver.manage().window().maximize();
            currentTitle = driver.getTitle();

            // Click on Math Calculators
            String y = RandomStringUtils.random(1, "123");
            String x = RandomStringUtils.random(1, "123456789");
            List<WebElement> elements = driver.findElements(By.tagName("iframe"));
            log.info("get ad frame size=" + elements.size());
            int frameIndex = RandomUtils.nextInt(2, elements.size());
            if(frameIndex == 4 || frameIndex == 5){
                frameIndex-=2;
            }
            log.info("switch to frame index=" + frameIndex);
            driver.switchTo().frame(frameIndex);

            WebElement webElement = driver.findElement(By.xpath("//div[@class='container']/div[" + y + "]/a[" + x + "]"));
            currentTitle = webElement.getText();
            currentUrl = webElement.getAttribute("href");
            log.info("click to keyword="+currentTitle+",url="+currentUrl);
            webElement.findElement(By.tagName("div")).click();
            log.info("is opend haosou,click at frameIndex="+frameIndex+", y=" + y + ",x=" + x);
            try {
                long waitTime = Math.round(pageOpenWaitTime * 1.3);
                log.info("after opened first ad page haosou wait for "+waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
            }


            Set<String> handlers = driver.getWindowHandles();
            String curentWindowHandler = driver.getWindowHandle();
            for (String handler : handlers) {
                if (StringUtils.equals(curentWindowHandler, handler)) {
                    continue;
                }
                WebDriver window = driver.switchTo().window(handler);
                if (StringUtils.equals(window.getCurrentUrl(), currentUrl)) {
                    break;
                }
            }
            log.info("swith to pop window " + driver.getTitle());

            String itemIndex = RandomStringUtils.random(1,"12345");
            WebElement secondAdEl = driver.findElement(By.xpath(".//*[@id='e_idea_pp']/li["+itemIndex+"]/h3/a"));

            currentUrl = secondAdEl.getAttribute("href");
            currentTitle = secondAdEl.getText();

            log.info("click to second keyword="+currentTitle+",url="+currentUrl);
            secondAdEl.click();  //hasou item
            log.info("is opend haosou item,click at itemIndex=" + itemIndex);
            try {
                long waitTime = Math.round(pageOpenWaitTime * 1.5);
                log.info("after opened second ad page haosou wait for "+waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException("currentTitle="+currentTitle+",currentUrl="+currentUrl,e);
        } finally {
            if(driver != null){
                driver.quit();
            }
        }

    }
}
