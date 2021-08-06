package com.microyu;

import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread implements Runnable {
    private final int threadID;
    private static int currentThreadNumber = 0;

    private boolean cancelFlag = false;
    private DownloadManager.Block block;

    public DownloadThread(DownloadManager.Block block) {
        DownloadThread.currentThreadNumber++;
        threadID = DownloadThread.currentThreadNumber;

        this.block = block;
    }

    public int getThreadID() {
        return threadID;
    }

    @Override
    public void run() {
        try {
            RandomAccessFile raf = new RandomAccessFile(block.getTargetFilePath(), "rwd");
            raf.seek(block.getStartPos());

            URL url = new URL(block.getDownloadLink());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + block.getStartPos() + "-" + block.getEndPos());
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] buffer = new byte[4096];
            int length = 0;
            while ((length = bis.read(buffer)) != -1) {
                raf.write(buffer, 0, length);
                block.setDownloadedSize(length, false);
                if (cancelFlag) {
                    break;
                }
            }

            conn.disconnect();
            bis.close();
            is.close();
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCancelFlag(boolean cancelFlag) {
        this.cancelFlag = cancelFlag;
    }
}
