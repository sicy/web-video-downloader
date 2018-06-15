package com.sicy.paipai;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

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
public class Test2 {
    public static void main(String[] args) {
        String seedUrl = "https://ac.ppdai.com/User/Login?message=&Redirect=";
        WebDriver driver = new ChromeDriver();
        driver.get(seedUrl);
        WebDriver driver2 = new ChromeDriver();
        driver2.get(seedUrl);
    }
}
