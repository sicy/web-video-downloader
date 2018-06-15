package com.sicy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.util.*;

/**
 * (填写类功能描述)
 *
 * @author SiChunyang
 * @version 1.0
 *          <p>
 *          <br/>
 *          <br/>修订人		修订时间			描述信息
 *          <br/>-----------------------------------------------------
 *          <br/>SiChunyang		2018/1/19		初始创建
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        String pptvUrl = "http://v.pptv.com/show/Uyrtaukb0A5x71c.html";
        String dir = "D:\\opt\\data\\video";

        long t1 = System.currentTimeMillis();
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);
        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        // configure it as a desired capability
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>(16);
        prefs.put("profile.default_content_setting_values.plugins", 1);
        prefs.put("profile.content_settings.plugin_whitelist.adobe-flash-player", 1);
        prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.adobe-flash-player", 1);
        // Enable Flash for this site
        options.setExperimentalOption("prefs", prefs);

        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        WebDriver driver = new ChromeDriver(capabilities);
        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        // create a new HAR with the label "yahoo.com"
        proxy.newHar("pptv.com");
        Thread thread1 = new Thread(()->{
            driver.get(pptvUrl);
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();

        String videoXmlUrl = "";
        String videoPlayUrl = "";
        loop: while (true){
            // get the HAR data
            Har har = proxy.getHar();
            try {
                List<HarEntry> entries = har.getLog().getEntries();
                System.out.println("请求数:" + entries.size());
                for (HarEntry e : entries){
                    if(e.getRequest() == null){
                        continue;
                    }
                    if("".equals(videoXmlUrl) && e.getRequest().getUrl().startsWith("http://web-play.pptv.com/webplay")){
                        videoXmlUrl = e.getRequest().getUrl();
                        System.out.println(videoXmlUrl);
                    }
                    if(!"".equals(videoXmlUrl) && e.getRequest().getUrl().contains(".mp4?fpp.ver=1.3.0.23&key=")){
                        videoPlayUrl = e.getRequest().getUrl();
                        System.out.println(videoPlayUrl);
                        break loop;
                    }
                }
            } catch (Exception e) {
                System.out.println("发生异常!!!!");
                e.printStackTrace();
            }
            Thread.sleep(5000);
        }
        thread1.interrupt();
        new Thread(driver::close).start();
        proxy.stop();

        //读取videoXmlUrl 内容
        try {
            System.out.println(videoXmlUrl);
            //获取key
            String k = CRequest.getValue(videoPlayUrl, "k");
            System.out.println("k::::" + k);
            String url = CRequest.UrlPage(videoPlayUrl);
            System.out.println(url);

            String[] t = url.substring(7).split("/");
            String videoName = t[t.length - 1];

            String videoXml = testGet(videoXmlUrl);
            int num = DOMTest.getVideoUrls(videoXml, videoName);
            System.out.println(num);

            for (int i = 0;i < num; i++){
                t[1] = i + "";
                StringBuilder sb = new StringBuilder();
                sb.append("http:/");
                for (String s : t){
                    sb.append("/");
                    sb.append(s);
                }
                String vu = sb.toString() + "?k=" + k + "&type=web.fpp";
                System.out.println(vu);

                //启动线程下载视频
                System.out.println("开始下载.." + vu);
                //拼接出文件名
                String surfix = videoName.substring(videoName.lastIndexOf("."), videoName.length());
                String fileName = videoName.substring(0, videoName.lastIndexOf("."));
                String filePath = dir + "/" + fileName + "_" + (i+1) + surfix;
                System.out.println("路径:" + filePath);
                ThreadDownload d = new ThreadDownload(vu, 10, fileName, filePath);
                try {
                    d.download();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while(d.getCompleteRate()<1){
                    double rate = d.getCompleteRate();
                    System.out.println("已完成:" +  d.getFileName() + "::" + (((rate * 100)+"").length() > 5 ? ((rate * 100)+"").substring(0, 5) : ((rate * 100)+"")) + "%" );
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();
        System.out.println("总耗时:" + (t2 - t1));
    }

    private static String testGet(String url) throws IOException {
        // TODO Auto-generated constructor stub
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        String strResult = "";
        if(response!=null) {
            HttpEntity entity = response.getEntity();
            strResult = EntityUtils.toString(entity,"UTF-8");
            EntityUtils.consume(entity);
        }
        httpget.abort();
        return strResult;
    }

}
