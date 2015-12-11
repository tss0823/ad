package com.yuntao.solm.demo;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;
import java.util.concurrent.*;

public class WebDriverDemo {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        //Puts a Implicit wait, Will wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        //Launch website
        driver.navigate().to("http://www.ccdidi.com/index.html");

        //Maximize the browser
        driver.manage().window().maximize();

        // Click on Math Calculators
        String y = RandomStringUtils.random(1, "123");
        String x = RandomStringUtils.random(1,"123456789");
        List<WebElement> elements = driver.findElements(By.tagName("iframe"));
        System.out.println("get ad frame size="+elements.size());
        int frameIndex = RandomUtils.nextInt(2,elements.size());
        System.out.println("frameIndex="+frameIndex);
        driver.switchTo().frame(frameIndex);

        System.out.println("x="+x+",y="+y);
        String href = driver.findElement(By.xpath("//div[@class='container']/div["+y+"]/a["+x+"]")).getAttribute("href");
        System.out.println("href="+href);
        driver.findElement(By.xpath("//div[@class='container']/div["+y+"]/a["+x+"]/div")).click();


        try{
            Thread.sleep(5000);
        }catch (Exception e){}

        Set<String> handlers = driver.getWindowHandles();
        String curentWindowHandler = driver.getWindowHandle();
        for(String handler : handlers){
            if(StringUtils.equals(curentWindowHandler,handler)){
                continue;
            }
            WebDriver window = driver.switchTo().window(handler);
            if(StringUtils.equals(window.getCurrentUrl(),href)){
                break;
            }
        }
        System.out.println("curentTitle="+driver.getTitle());

        try{
            Thread.sleep(3000);
        }catch (Exception e){}
//        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//        driver.switchTo().window()
        // second

        String adHref = driver.findElement(By.xpath("//div[@id='m-spread-left']/ul[1]/li[" + 1 + "]/h3[1]/a[1]")).getAttribute("href");  //fref
        System.out.println("adHref="+adHref);
        driver.findElement(By.xpath("//div[@id='m-spread-left']/ul[1]/li[" + 1 + "]/h3[1]/a[1]")).click();  //µã»÷hasou item


        try{
            Thread.sleep(6000);
        }catch (Exception e){}
        //Close the Browser.
        driver.quit();
    }
}