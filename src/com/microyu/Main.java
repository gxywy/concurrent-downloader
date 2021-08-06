package com.microyu;

import org.junit.Test;

public class Main {

    public static void main(String[] args) throws Exception {
        String downloadLink = "https://down.qq.com/qqweb/PCQQ/PCQQ_EXE/PCQQ2021.exe";
        Task task = new Task(downloadLink, "");

        DownloadManager dm = new DownloadManager(task);
        dm.setThreadNum(16);
        dm.startDownload();
        Thread.sleep(5000);
        dm.pauseDownload();
        Thread.sleep(5000);
        dm.resumeDownload();
    }
}
