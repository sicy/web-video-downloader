package com.sicy;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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
public class HttpDownload {
    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.getParentFile().exists()) {
            saveDir.getParentFile().mkdir();
        }
        File file = new File(savePath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        fos.close();
        if (inputStream != null) {
            inputStream.close();
        }
        System.out.println("info:" + url + " download success");
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        String url = "http://113.207.86.9/play.videocache.lecloud.com/164/18/65/letv-uts/14/ver_00_22-325504776-avc-480112-aac-32000-5998966-391334366-dfc00e230955d783a44ce67e01f160f8-1440682783313_mp4/ver_00_22_13_26_4_1015764_9953848_0.ts?crypt=65aa7f2e586&b=521&nlh=4096&nlt=60&bf=90&p2p=1&video_type=mp4&termid=1&tss=ios&platid=1&splatid=105&its=0&qos=3&fcheck=0&amltag=100&mltag=100&proxy=3721186890,3721176775,467484312&uid=1884300034.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1516794600&nkey=f6acc1dc76fcde41ecff4387ded9279b&nkey2=b5f8966a9624e27990cc9b27b229ccd6&auth_key=1516794600-1-0-1-105-b03f9ad9e93a8c91c7fbb904fe615579&geo=CN-10-127-2&mmsid=1763883&tm=1516776531&key=3eca3691e2602fe55be5c4b2bb0e3eeb&playid=0&vtype=13&cvid=16480485580&payff=0&m3v=1&hwtype=un&ostype=Windows10&p1=1&p2=10&p3=-&tn=0.11057159677147865&vid=1610838&uuid=50D4BD21B2A7D5065C4A6FEE630CD8790528C4EE_0&sign=letv&uidx=0&errc=0&gn=3311&ndtype=0&vrtmcd=102&buss=100&cips=112.80.35.2&r=1516776613988&appid=500";
        try {
            downLoadFromUrl(url, "D:\\opt\\data\\letv\\v1.ts");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
