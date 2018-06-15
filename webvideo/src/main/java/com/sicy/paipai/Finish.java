package com.sicy.paipai;

import com.alibaba.fastjson.JSONObject;
import com.xunsiya.tools.common.excel.ExcelUtil;

import java.io.*;
import java.sql.SQLException;
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
 *          <br/>SiChunyang		2018/5/17		初始创建
 */
public class Finish {

    public static void main(String[] args) throws Exception {
        DbUtils dbUtils = DbUtils.getIntence();

        List<String[]> excelList = new ArrayList<>();


        Map<String, Map<String, Object>> cache = new HashMap<>(2048);

        BufferedReader br = new BufferedReader(new FileReader(new File("F:/pp.txt")));
        String line;
        int i= 0;
        while((line = br.readLine())!=null){
            i++;
            JSONObject jsonObject = JSONObject.parseObject(line);
            String url = jsonObject.getString("href");
//            {
//                "brate": "11%",
//                    "href": "//invest.ppdai.com/loan/info?id=112106473",
//                    "limitTime": "9个月",
//                    "listtitle": "手机app用户的借款",
//                    "name": "pdu4****26004",
//                    "process": "已有26人投标 已完成30%",
//                    "sum": "¥20,000"
//            }

            //主要字段
            String brate = jsonObject.getString("brate");
            String limitTime = jsonObject.getString("limitTime");
            String listtitle = jsonObject.getString("listtitle");
            String name = jsonObject.getString("name");
            String process = jsonObject.getString("process");
            String sum = jsonObject.getString("sum");

            Map<String, Object> userInfo = new HashMap<>(16);
            if(cache.containsKey(url)){
                userInfo = cache.get(url);
            }else{
                //查询数据库
                List<Map<String, Object>> l = dbUtils.find("select * from userinfo where url = ?", url);
                if(l != null && l.size() > 0){
                    userInfo = l.get(0);
                    cache.put(url, userInfo);
                }
            }

            String[] data = new String[]{
                    listtitle,
                    name,
                    sum,
                    process,
                    brate,
                    userInfo.get("sex")+"",
                    userInfo.get("age")+"",
                    userInfo.get("registdate")+"",
                    userInfo.get("degree")+"",
                    userInfo.get("school")+"",
                    userInfo.get("job")+"",
                    userInfo.get("income")+"",
                    "http:"+url
            };
            excelList.add(data);
            System.out.println("完成:"+i);
        }


        //生成excel
        String filePathExp = "F:/finish.xls";
        String[] title = new String[] {"标题", "姓名", "金额", "进度", "利率", "性别", "年龄", "注册时间", "学历", "毕业学校", "工作", "收入情况"};
        ExcelUtil.createExcel(filePathExp, title, null, excelList);

    }

}
