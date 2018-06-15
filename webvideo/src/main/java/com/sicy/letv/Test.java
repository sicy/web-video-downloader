package com.sicy.letv;

import com.sicy.HttpDownload;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (填写类功能描述)
 *
 * @author SiChunyang
 * @version 1.0
 *          <p>
 *          <br/>
 *          <br/>修订人		修订时间			描述信息
 *          <br/>-----------------------------------------------------
 *          <br/>SiChunyang		2018/1/24		初始创建
 */
public class Test {

    public static void main(String[] args) {
        String videoPageUrl = "http://www.le.com/ptv/vplay/20732649.html";
        String hostName = "le.com";
        String dir = "D:\\opt\\data\\letv\\1";

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
        proxy.newHar(hostName);
        Thread thread1 = new Thread(()->{
            driver.get(videoPageUrl);
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                //ignore
            }
        });

        thread1.start();
        String mediaInfoUrl = "";
        loop: while (true){
            // get the HAR data
            Har har = proxy.newHar(hostName);
            try {
                List<HarEntry> entries = har.getLog().getEntries();
                for (HarEntry e : entries){
                    String url = e.getRequest().getUrl();
                    if(url.contains(".m3u8?")){
                        mediaInfoUrl = url;
                        break loop;
                    }
                }
            } catch (Exception e) {
                System.out.println("发生异常!!!!");
                e.printStackTrace();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread1.interrupt();
        new Thread(driver::close).start();
        proxy.stop();

        //下载分析mediaInfoUrl文件
        String mediaSaveFile = "D:\\opt\\data\\letv\\aaa";
        try {
            HttpDownload.downLoadFromUrl(mediaInfoUrl, mediaSaveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //读取内容  获取视频链接
        List<String> videoUrls = new ArrayList<>();
        try{
            FileReader f = new FileReader(new File(mediaSaveFile));
            BufferedReader br = new BufferedReader(f);
            String t;
            while((t = br.readLine()) != null){
                if(t.startsWith("http://")){
                    videoUrls.add(t);
                }
            }
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //直接下载
        for(int i = 0 ;i < videoUrls.size(); i++){
            System.out.println("开始下载:" + i + ".ts");
            String url = videoUrls.get(i);
            String fileName = dir + "/" + i +".ts";
            try {
                HttpDownload.downLoadFromUrl(url, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("下载完成. 耗时:" + (t2 - t1));

    }

}
