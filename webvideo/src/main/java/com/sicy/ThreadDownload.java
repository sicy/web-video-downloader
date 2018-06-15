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
public class ThreadDownload {
    private String urlPath;

    private int threadNum;

    private DownThread[] threads;

    private long fileSize;

    private String fileName;

    private String filePath;

    public ThreadDownload(String url, int threadNum, String fileName, String filePath){
        this.threadNum = threadNum;
        this.urlPath = url;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public static void main(String[] args) throws IOException {
        String path = "http://v3-tt.ixigua.com/e4306286f527b6a7ea9afed177c962bc/5b0e6791/video/m/2206b8c564e29e94c7fbadd59baddb1c4b111577bd500007ab3df6dd0e3/";
        ThreadDownload d = new ThreadDownload(path, 10, "ccc.mp4", "D:\\opt\\data\\ccc.mp4");
        d.download();
        new Thread(() -> {
            long t1 = System.currentTimeMillis();
            while(d.getCompleteRate()<1){
                System.out.println("已完成:" +  (d.getCompleteRate()* 100)  + "%");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long t2 = System.currentTimeMillis();
            System.out.println("耗时:" + (t2 - t1));
        }).start();
    }

    public void download() throws IOException {
        this.fileSize = DownloadTest.getRemoteFileSzie(urlPath);
        File targetFile = new File(filePath);
        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        // 设置本地文件的大小
        file.setLength(fileSize);
        file.close();


        long currentPartSize = fileSize / threadNum;
        this.threads = new DownThread[threadNum];

        for (int i = 0; i < threadNum; i++) {
            // 计算每条线程的下载的开始位置
            long startPos = i * currentPartSize;
            // 每个线程使用一个RandomAccessFile进行下载
            RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
            // 定位该线程的下载位置
            currentPart.seek(startPos);
            // 创建下载线程
            threads[i] = new DownThread(startPos, currentPartSize, currentPart);
            // 启动下载线程
            threads[i].start();
        }
    }

    /**
     * 获取下载的完成百分比
     * @return
     */
    public double getCompleteRate() {
        // 统计多条线程已经下载的总大小
        int sumSize = 0;
        for (int i = 0; i < threadNum; i++) {
            sumSize += threads[i].getLength();
        }
        // 返回已经完成的百分比
        return sumSize * 1.0 / fileSize;
    }

    public String getFileName(){
        return fileName;
    }



    private class DownThread extends Thread{
        /**
         * 当前线程的下载位置
         */
        private long startPos;
        /**
         * 定义当前线程负责下载的文件大小
         */
        private long currentPartSize;
        /**
         * 当前线程需要下载的文件块
         */
        private RandomAccessFile currentPart;
        /**
         * 定义已经该线程已下载的字节数
         */
        private int length;

        private DownThread(long startPos, long currentPartSize, RandomAccessFile currentPart){
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPart = currentPart;
        }

        public int getLength(){
            return length;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(urlPath);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 设置User-Agent
                conn.setRequestProperty("User-Agent", "Net");
                // 设置续传开始
                conn.setRequestProperty("Range", "bytes=" + startPos + "-");
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

                byte[] buffer = new byte[10240];
                int hasRead = 0;
                // 读取网络数据，并写入本地文件
                while (length < currentPartSize && (hasRead = bis.read(buffer)) != -1) {
                    currentPart.write(buffer, 0, hasRead);
                    // 累计该线程下载的总大小
                    length += hasRead;
                }
                currentPart.close();
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}

