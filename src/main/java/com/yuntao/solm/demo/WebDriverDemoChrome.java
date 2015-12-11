package com.yuntao.solm.demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebDriverDemoChrome {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:\\selenium_driver\\IEDriverServer.exe");

        //关闭保护模式
        DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
        cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        //IE默认启动保护模式，要么手动在浏览器的设置中关闭保护模式，要么在代码中加上这一句，即可
        cap.setCapability("ignoreProtectedModeSettings", true);

// 代理配置
        String proxyIpAndPort = "220.202.123.42:55336";


        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
// 配置http、ftp、ssl代理（注：当前版本只支持所有的协议公用http协议，下述代码等同于只配置http）
        proxy.setHttpProxy(proxyIpAndPort)
                .setFtpProxy(proxyIpAndPort)
                .setSslProxy(proxyIpAndPort);

// 以下三行是为了避免localhost和selenium driver的也使用代理，务必要加，否则无法与iedriver通讯
        cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
        cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
        System.setProperty("http.nonProxyHosts", "localhost");

        cap.setCapability(CapabilityType.PROXY, proxy);
        WebDriver driver = new InternetExplorerDriver(cap);

//        Proxy proxy = new Proxy();

        //Launch website
        driver.navigate().to("http://test.bos.yuntaohongbao.com/login.html?test=abc1");

        //Maximize the browser
        driver.manage().window().maximize();

        driver.findElement(By.xpath(".//div[2]/div[2]/div/div/form/div[2]/div[1]/input")).sendKeys("tangshengshan");

        driver.quit();
    }
}