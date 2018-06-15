package com.sicy;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

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
public class DownloadTest {

    public interface HttpClientDownLoadProgress {
        public void onProgress(int progress);
    }

    private static void httpDownloadFile(String url, String filePath,
                                         HttpClientDownLoadProgress progress, Map<String, String> headMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            setGetHead(httpGet, headMap);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            try {
                System.out.println(response1.getStatusLine());
                HttpEntity httpEntity = response1.getEntity();
                long contentLength = httpEntity.getContentLength();
                InputStream is = httpEntity.getContent();
                // 根据InputStream 下载文件
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int r = 0;
                long totalRead = 0;
                while ((r = is.read(buffer)) > 0) {
                    output.write(buffer, 0, r);
                    totalRead += r;
                    if (progress != null) {
                        progress.onProgress((int) (totalRead * 100 / contentLength));
                    }
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                output.writeTo(fos);
                output.flush();
                output.close();
                fos.close();
                EntityUtils.consume(httpEntity);
            } finally {
                response1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置http的HEAD
     *
     * @param httpGet
     * @param headMap
     */
    private static void setGetHead(HttpGet httpGet, Map<String, String> headMap) {
        if (headMap != null && headMap.size() > 0) {
            Set<String> keySet = headMap.keySet();
            for (String key : keySet) {
                httpGet.addHeader(key, headMap.get(key));
            }
        }
    }


    public static void main(String[] args) {
        HttpURLConnection httpURLConnection = null;
        URL url = null;
        BufferedInputStream bis = null;
        byte[] buf = new byte[10240];
        int size = 0;
        String fileName = "1da5e170161855412a96213e22f6b19e.mp4";
        String filePath = "D:\\opt\\data\\video";
        String remoteUrl = "http://153.3.49.217/0/0/1024/1da5e170161855412a96213e22f6b19e.mp4?k=fd11d13ccd753abf7e9412eff4d2fd1b-dbc2-1516621527%26bppcataid%3d38&type=web.fpp";

        // 检查本地文件
        RandomAccessFile rndFile = null;
        File file = new File(filePath + "\\" + fileName);
        long remoteFileSize = getRemoteFileSzie(remoteUrl);
        System.out.println(remoteFileSize);
        long nPos = 0;

        if (file.exists()) {
            long localFileSzie = file.length();
            if (localFileSzie < remoteFileSize) {
                System.out.println("文件续传...");
                nPos = localFileSzie;
            } else {
                System.out.println("文件存在，重新下载...");
                file.delete();
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 下载文件
        try {
            url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置User-Agent
            httpURLConnection.setRequestProperty("User-Agent", "Net");
            // 设置续传开始
            httpURLConnection.setRequestProperty("Range", "bytes=" + nPos + "-");
            // 获取输入流
            bis = new BufferedInputStream(httpURLConnection.getInputStream());
            rndFile = new RandomAccessFile(filePath + "\\" + fileName, "rw");
            rndFile.seek(nPos);
            int i = 0;
            while ((size = bis.read(buf)) != -1) {
                //if (i > 500) break;
                long t1 = System.currentTimeMillis();
                rndFile.write(buf, 0, size);
                long t2 = System.currentTimeMillis();
                System.out.println(t2 - t1);
                i++;
            }
            System.out.println("i=" + i);
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getRemoteFileSzie(String url) {
        long size = 0;
        try {
            HttpURLConnection httpUrl = (HttpURLConnection) (new URL(url)).openConnection();
            size = httpUrl.getContentLength();
            if(httpUrl.getHeaderField("Content-Range") != null){
                String[] ss = httpUrl.getHeaderField("Content-Range").split("[/]");
                if (ss.length > 1){
                    size = Integer.parseInt(ss[1]);
                }
            }
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}
