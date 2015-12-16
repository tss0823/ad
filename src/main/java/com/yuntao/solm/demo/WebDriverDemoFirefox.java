package com.yuntao.solm.demo;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.*;

public class WebDriverDemoFirefox {
    public static void main(String[] args) {

        WebDriver driver = new FirefoxDriver();

        //Launch website
        driver.navigate().to("http://2diancan.com/index.html");

        driver.manage().window().maximize();

        List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@id,\"QIHOO_UNION_\")]"));
        int frameIndex = RandomUtils.nextInt(0, elements.size());

        driver.switchTo().frame(frameIndex);


//            String souXpath = "//div[@class='container']/div[@class='row']["+y+"]/a[" + x + "]";
//            String countSouXpath = "//div[@class='container']/div[@class='row']["+y+"]/a";
        WebElement clickWebEle = null;
        String souXpathY = "//div[@class='container' or @id='container']/div[@class='row']";
        List<WebElement> elesY = driver.findElements(By.xpath(souXpathY));
        int y = 0;
        int x = 0;
        if(elesY.size() > 1){
            y = RandomUtils.nextInt(0,elesY.size());
        }
        List<WebElement> hrefEles = elesY.get(y).findElements(By.xpath("a"));
        if(hrefEles.size() > 1){
            x = RandomUtils.nextInt(0,hrefEles.size());
        }

        clickWebEle = hrefEles.get(x);
        String currentTitle = clickWebEle.getText();
        String currentUrl = clickWebEle.getAttribute("href");
        System.out.println("click to keyword="+currentTitle+",url="+currentUrl);
        clickWebEle.findElement(By.tagName("div")).click();

        //Maximize the browser

        try {
            Thread.sleep(6000);
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

        String itemIndex = RandomStringUtils.random(1, "12345");
        WebElement secondAdEl = driver.findElement(By.xpath(".//*[@id='e_idea_pp']/li[" + itemIndex + "]/h3/a"));

        currentUrl = secondAdEl.getAttribute("href");
        currentTitle = secondAdEl.getText();

        System.out.println("click to second keyword=" + currentTitle + ",url=" + currentUrl);
        secondAdEl.click();  //hasou item
        System.out.println("is opend haosou item,click at itemIndex=" + itemIndex);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
    }
}