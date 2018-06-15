package com.sicy.paipai;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.sql.SQLException;
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
public class UserMain3 {

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
//        Set<String> urls = new HashSet<>();
//        BufferedReader br = new BufferedReader(new FileReader(new File("F:/pp.txt")));
//        String line;
//        while((line = br.readLine())!=null){
//            JSONObject info = JSONObject.parseObject(line);
//            //取出url  放入去重set
//            String url = info.getString("href");
//            if(!urls.contains(url)){
//                urls.add(url);
//                System.out.println(url);
//            }
//        }
//        System.out.println(urls.size());
//        FileUtils.writeLines(new File("F:/urls.txt"), Arrays.asList(urls.toArray()));
        DbUtils dbUtils = DbUtils.getIntence();

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
            System.out.println("请输入验证码:::::::");
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            String str = strin.readLine();
            imgyzm.sendKeys(str);
            loginbtn.click();
        }
        Thread.sleep(3000);

        BufferedReader br = new BufferedReader(new FileReader(new File("F:/urls3.txt")));
        String line;
        int i= 0;
        while((line = br.readLine())!=null){
            i++;
            if(i%3==2){
                String url = "https:" + line;
                System.out.println("---开始爬取--"+i + "  --个页面..");
                driver.get(url);

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

                try{
                    dbUtils.update("insert into userinfo(url, sex, age, registdate, degree, school, job, income)values" +
                            "(?, ?, ?, ?, ?, ?, ?, ?)", line, sex, age, registDate, degree, school, job, income);
                }catch (MySQLIntegrityConstraintViolationException e){
                    //do nothing
                }
                int num = (int)(100 * new Random().nextDouble());
                Thread.sleep(num);
            }
        }

    }

}
