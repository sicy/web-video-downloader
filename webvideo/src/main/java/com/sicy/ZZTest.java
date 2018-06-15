package com.sicy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

/**
 * (填写类功能描述)
 *
 * @author SiChunyang
 * @version 1.0
 *          <p>
 *          <br/>
 *          <br/>修订人		修订时间			描述信息
 *          <br/>-----------------------------------------------------
 *          <br/>SiChunyang		2018/1/22		初始创建
 */
public class ZZTest {
    public static void main(String[] args) throws Exception {
        String seedUrl = "https://ac.ppdai.com/User/Login?message=&Redirect=";
        WebDriver driver = new ChromeDriver();
//        Thread t = new Thread(() -> driver.get(seedUrl));
//        t.start();
//        t.join(5000);
//        WebElement username = driver.findElement(By.id("UserName"));
//        username.sendKeys("15951752961");
//        WebElement pwd = driver.findElement(By.id("Password"));
//        pwd.sendKeys("1q2w3e4r");
//        WebElement loginbtn = driver.findElement(By.id("login_btn"));
//        loginbtn.click();
//        Thread.sleep(3000);

//        driver.get("https://invest.ppdai.com/loan/info?id=112714986");
        driver.get("https://invest.ppdai.com/loan/listnew");
        String html_txt = driver.getPageSource();
        Document doc = Jsoup.parse(html_txt);
        // body > div.main > div.w1000center > div.borrowListContent > div.outerBorrowList > div > ol:nth-child(1)
        Elements es = doc.select("body > div.main > div.w1000center > div.borrowListContent > div.outerBorrowList > div > ol.clearfix");
        for (Element e : es) {
            System.out.println(e.select("ol > li > div.w230.listtitle > p > a").get(0).text());
            System.out.println("---------------------------");
        }
        driver.close();
    }
}
