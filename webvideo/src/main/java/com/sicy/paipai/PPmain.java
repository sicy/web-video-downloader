package com.sicy.paipai;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * (填写类功能描述)
 *
 * @author SiChunyang
 * @version 1.0
 *          <p>
 *          <br/>
 *          <br/>修订人		修订时间			描述信息
 *          <br/>-----------------------------------------------------
 *          <br/>SiChunyang		2018/5/16		初始创建
 */
public class PPmain {

    public static void main(String[] args) throws InterruptedException, IOException {
        int pageIndex = 1;

        String seedUrl = "https://ac.ppdai.com/User/Login?message=&Redirect=";
        WebDriver driver = new ChromeDriver();
        Thread t = new Thread(() -> driver.get(seedUrl));
        t.start();
        t.join(5000);
        WebElement username = driver.findElement(By.id("UserName"));
        username.sendKeys("15951752961");
        WebElement pwd = driver.findElement(By.id("Password"));
        pwd.sendKeys("1q2w3e4r");
        WebElement loginbtn = driver.findElement(By.id("login_btn"));
        loginbtn.click();
        Thread.sleep(3000);
        //imgyzm
        WebElement imgyzm = driver.findElement(By.id("imgyzm"));
        if(imgyzm.isDisplayed()){
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            String str = strin.readLine();
            imgyzm.sendKeys(str);
            loginbtn.click();
        }
        Thread.sleep(3000);

        String url;
        while (true){
            System.out.println("----------开始爬取第"+pageIndex+"页-----");
            url = "https://invest.ppdai.com/loan/listnew?LoanCategoryId=0&SortType=0&PageIndex="+pageIndex+"&MinAmount=0&MaxAmount=0";
            driver.get(url);

            String html_txt = driver.getPageSource();
            Document doc = Jsoup.parse(html_txt);
            // body > div.main > div.w1000center > div.borrowListContent > div.outerBorrowList > div > ol:nth-child(1)
            Elements elements = doc.select("body > div.main > div.w1000center > div.borrowListContent > div.outerBorrowList > div > ol.clearfix");
            if(elements.size() == 0){
                break;
            }
            List<String> lines = new ArrayList<>();
            for (Element e : elements) {
                String href = e.select("ol > li > div.w230.listtitle > a").get(0).attr("href");
                String listtitle = e.select("ol > li > div.w230.listtitle > a").get(0).text();
                String name = e.select("ol > li > div.w230.listtitle > p > a").get(0).text();
                String brate = e.select("ol > li > div.w110.brate").get(0).text();
                String sum = e.select("ol > li > div.w90.sum").get(0).text();
                String limitTime = e.select("ol > li > div.w82.limitTime").get(0).text();
                String process = e.select("ol > li > div.w140.process > p").get(0).text();
                String click = e.select("ol > li > div.w140.operate > a").get(0).attr("onclick");

                System.out.println(href);
                System.out.println(listtitle);
                System.out.println(name);
                System.out.println(brate);
                System.out.println(sum);
                System.out.println(limitTime);
                System.out.println(process);
                System.out.println(click);
                System.out.println("---------------------------");
                PpVo vo = new PpVo();
                vo.setHref(href);
                vo.setListtitle(listtitle);
                vo.setName(name);
                vo.setBrate(brate);
                vo.setSum(sum);
                vo.setProcess(process);
                vo.setLimitTime(limitTime);
                vo.setClick(click);
                String line = JSONObject.toJSONString(vo);
                lines.add(line);
            }

            FileUtils.writeLines(new File("F:/pp.txt"), lines, true);

            pageIndex++;
            int num = (int)(2999 * new Random().nextDouble());
            Thread.sleep(num);
        }
        driver.close();
    }

}
