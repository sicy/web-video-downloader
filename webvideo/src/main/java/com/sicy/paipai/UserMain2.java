package com.sicy.paipai;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class UserMain2 {

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

        BufferedReader br = new BufferedReader(new FileReader(new File("F:/error.txt")));
        String line;
        int i= 0;
        while((line = br.readLine())!=null){
            i++;
            String url = line;
            System.out.println("---开始爬取--"+i + "  --个页面.."+url);
            driver.get(url);

            String html_txt = driver.getPageSource();
            Document doc = Jsoup.parse(html_txt);
            try{
//                String sex = doc.select(".lender-info > p:nth-child(1) > span").get(0).text();
//                String age = doc.select(".lender-info > p:nth-child(2) > span").get(0).text();
//                String registDate = doc.select(".lender-info > p:nth-child(3) > span").get(0).text();
//                String degree = doc.select(".lender-info > p:nth-child(4) > span").get(0).text();
//                String school = doc.select(".lender-info > p:nth-child(5) > span").get(0).text();
//                String job = doc.select(".lender-info > p:nth-child(10) > span").get(0).text();
//                String income = doc.select(".lender-info > p:nth-child(11) > span").get(0).text();
                //body > div.main > div.lendDetailTab_tabContent.w1000center > div:nth-child(1) > div > p:nth-child(9) > span
                String sex = "";
                String age = "";
                String registDate = "";
                String degree = "";
                String school = "";
                String job = "";
                String income = "";
                Elements elements = doc.select(".lender-info > p.ex.col-1");
                for (Element e : elements){
                    if(e.text().contains("性别")){
                        sex = e.select("span").get(0).text();
                    }else if(e.text().contains("年龄")){
                        age = e.select("span").get(0).text();
                    }else if(e.text().contains("注册时间")){
                        registDate = e.select("span").get(0).text();
                    }else if(e.text().contains("文化程度")){
                        degree = e.select("span").get(0).text();
                    }else if(e.text().contains("毕业院校")){
                        school = e.select("span").get(0).text();
                    }else if(e.text().contains("工作信息")){
                        job = e.select("span").get(0).text();
                    }else if(e.text().contains("收入情况")){
                        income = e.select("span").get(0).text();
                    }
                }
                System.out.println("sex: "+sex);
                System.out.println("age: "+age);
                System.out.println("re: "+registDate);
                System.out.println("degree: "+degree);
                System.out.println("school: "+school);
                System.out.println("job: "+job);
                System.out.println("income: "+income);

                dbUtils.update("insert into userinfo(url, sex, age, registdate, degree, school, job, income)values" +
                        "(?, ?, ?, ?, ?, ?, ?, ?)", line, sex, age, registDate, degree, school, job, income);
            }catch (MySQLIntegrityConstraintViolationException | IndexOutOfBoundsException e2){
                //do nothing
            }
            int num = (int)(100 * new Random().nextDouble());
            Thread.sleep(num);

        }

    }

}
