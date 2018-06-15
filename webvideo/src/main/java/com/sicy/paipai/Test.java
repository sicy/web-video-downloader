package com.sicy.paipai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
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


        driver.get("https://invest.ppdai.com/loan/info?id=112718603");

        String html_txt = driver.getPageSource();
        Document doc = Jsoup.parse(html_txt);
        String sex = doc.select(".lender-info > p:nth-child(1) > span").get(0).text();
        String age = doc.select(".lender-info > p:nth-child(2) > span").get(0).text();
        String registDate = doc.select(".lender-info > p:nth-child(3) > span").get(0).text();
        String degree = doc.select(".lender-info > p:nth-child(4) > span").get(0).text();
        String school = doc.select(".lender-info > p:nth-child(5) > span").get(0).text();
        String job = doc.select(".lender-info > p:nth-child(10) > span").get(0).text();
        String income = doc.select(".lender-info > p:nth-child(11) > span").get(0).text();
        System.out.println(sex);
        System.out.println(age);
        System.out.println(registDate);
        System.out.println(degree);
        System.out.println(school);
        System.out.println(job);
        System.out.println(income);

    }

}
