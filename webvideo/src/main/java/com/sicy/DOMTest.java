package com.sicy;


import org.dom4j.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.swing.text.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class DOMTest {

    public static int getVideoUrls(String xmlText, String videoName){
        // 创建SAXReader的对象reader
        SAXReader reader = new SAXReader();
        try {
            // 通过reader对象的read方法加载books.xml文件,获取docuemnt对象。
            Document document = DocumentHelper.parseText(xmlText);
            // 通过document对象获取根节点bookstore
            Element root = document.getRootElement();
            // 通过element对象的elementIterator方法获取迭代器
            Iterator it = root.elementIterator();
            // 遍历迭代器，获取根节点中的信息
            int num = 0;
            String ft = "";
            while (it.hasNext()) {
                Element child = (Element) it.next();
                String childName = child.getName();
                if("channel".equals(childName)){
                    Element file = child.element("file");
                    Iterator itt = file.elementIterator();
                    while(itt.hasNext()){
                        Element cc = (Element) itt.next();
                        if(cc.attribute("rid").getValue().equals(videoName)){
                            ft = cc.attributeValue("ft");
                        }
                    }
                }
                //获取视频分割长度
                if (child.attribute("ft") != null){
                    if(ft.equals(child.attribute("ft").getValue()) && "dragdata".equals(childName)){
                        Iterator itt = child.elementIterator();
                        while(itt.hasNext()){
                            Element cc = (Element) itt.next();
                            if("sgm".equals(cc.getName())){
                                num++;
                            }
                        }
                        System.out.println(num);
                    }
                }
            }
            return num;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
