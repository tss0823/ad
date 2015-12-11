package com.yuntao.solm.demo;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by shengshan.tang on 11/13/2015 at 11:23 PM
 */
public class SoLmTest {

    public static void main(String[] args) {
        try{
            String xml = getAdIndexHtml("http://www.ccdidi.com/index.html");
//            getFrameUrl(null);
//            getHaosouUrl(null);
//            getHaosouContent("http://www.haosou.com/s?src=lm&ls=s583b2f798d&q=%E9%80%9A%E5%AE%9D%E6%B8%B8%E6%88%8F&lmsid=c5d32b1961b09ca4&lm_extend=ctype:4");
//            getAdFrameContent("http://api.so.lianmeng.360.cn/searchthrow/api/ads/throw?ls=s386d56ad8b&w=120&h=300&inject=2&pos=4&rurl=http%3A%2F%2Fwww.ccdidi.com%2Findex.html&pn=0&prt=1448010634846&tit=&pt=1448010634801&cw=1256&jv=1437124819535&inlay=0&link=10&hao360=50&rank=10&imagelink=3");
//              gethaosouItemUrl(null);
        }catch (Exception e){

            e.printStackTrace();
        }

    }

    public static String getAdIndexHtml(String url)  {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try{
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setTimeout(15000);
            final HtmlPage page = webClient.getPage(url);
            String xml = page.asXml();
            Set<Cookie> cookies = webClient.getCookieManager().getCookies();
//            if(cookies != null && cookies.size() > 0){
//                for(Cookie cookie : cookies){
//                    System.out.println(cookie.toString());
//                }
//            }
            List<FrameWindow> frames = page.getFrames();
            if(CollectionUtils.isNotEmpty(frames)){
                for(FrameWindow fw : frames){
                    String iframeUrl = fw.getFrameElement().getAttribute("src");
                    getAdFrameContent(iframeUrl,cookies);
//                    System.out.println(framePage.asXml());
                    break;
                }
            }
            //获取ifrrame的url

            return xml;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            webClient.closeAllWindows();
        }
        return null;

    }

    public static List<String> getFrameUrl(String xml) throws IOException {
        xml = FileUtils.readFileToString(new File("d:/test.txt"));
        Element bodyEle = Jsoup.parse(xml).body();
        Elements eles = bodyEle.select("iframe");
        if(eles == null || eles.size() == 0){
            return null;
        }
        List<String> srcList = new ArrayList<String>();
        for(Element ele : eles){
            String src = ele.attr("src");
            srcList.add(src);
            System.out.println(src);
        }
        return srcList;
    }

    /**
     * TODO etcookie
     * @param frameUrl
     * @return
     */
    public static String getAdFrameContent(String frameUrl,Set<Cookie> cookies)  {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try{
            webClient.getCookieManager().setCookiesEnabled(true);
            if(cookies != null && cookies.size() > 0){
                for(Cookie cookie : cookies){
                    webClient.getCookieManager().addCookie(cookie);
                }
            }
            webClient.addRequestHeader("Upgrade-Insecure-Requests","1");
            webClient.addRequestHeader("Referer","http://www.ccdidi.com/index.html");
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            final HtmlPage page = webClient.getPage(frameUrl);
            String xml = page.asXml();
            return xml;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            webClient.closeAllWindows();
        }
        return null;

    }


    public static List<String> getHaosouUrl(String html) throws IOException {
        html = FileUtils.readFileToString(new File("d:/test2.txt"));
        Element bodyEle = Jsoup.parse(html).body();
        Element divEle = bodyEle.select("div.container").get(0);
        Elements linkEles = divEle.select("a[href]");
        List<String> srcList = new ArrayList<String>();
        for(Element ele : linkEles){
            String src = ele.attr("href");
            srcList.add(src);
            System.out.println(src);
        }
        return srcList;
    }


    public static String getHaosouContent(String itemUrl)  {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try{
            webClient.addRequestHeader("Upgrade-Insecure-Requests","1");
            webClient.addRequestHeader("Referer","http://api.so.lianmeng.360.cn/searchthrow/api/ads/throw?ls=s583b2f798d&w=960&h=90&inject=1&pos=0&rurl=http%3A%2F%2Fwww.ccdidi.com%2Findex.html&pn=0&prt=1447989158413&tit=&pt=1447989158346&cw=1536&jv=1437124819535&inlay=4&link=27&hao360=50&rank=24&imagelink=8");
            webClient.getCookieManager().setCookiesEnabled(true);
            final HtmlPage page = webClient.getPage(itemUrl);
            webClient.getOptions().setCssEnabled(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setTimeout(15000);
            String xml = page.asXml();
            Set<Cookie> cookies = webClient.getCookieManager().getCookies();
            if(cookies != null && cookies.size() > 0){
                for(Cookie cookie : cookies){
                    System.out.println(cookie.toString());
                }
            }
            return xml;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            webClient.closeAllWindows();
        }
        return null;

    }


    public static List<String> gethaosouItemUrl(String html) throws IOException {
        html = FileUtils.readFileToString(new File("d:/test3.txt"));
        Element bodyEle = Jsoup.parse(html).body();
        Element divEle = bodyEle.select("ul.e_clearfix").get(0);
        Elements linkEles = divEle.select("h3 > a[href]");
        List<String> srcList = new ArrayList<String>();
        for(Element ele : linkEles){
            String src = ele.attr("href");
            srcList.add(src);
            System.out.println(src);
        }
        return srcList;
    }

}
