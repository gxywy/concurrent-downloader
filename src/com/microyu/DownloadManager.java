package com.microyu;

import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager implements Runnable, Serializable {
    private int THREAD_NUM = 16;

    private Task task;
    private long taskLength;
    private long downloadedLength;
    private long blockSize;

    private DownloadStatus downloadStatus;
    private ArrayList<Block> blockList = new ArrayList<>();
    private ArrayList<DownloadThread> downloadThreads = new ArrayList<>();
    private ExecutorService es;
    private ProcessBar pb = new ProcessBar();

    public DownloadManager(Task task) throws IOException {
        this.task = task;
        this.taskLength = task.getTaskLength();
        this.blockSize = this.taskLength / this.THREAD_NUM;
        for (int i = 0; i < THREAD_NUM; i++) {
            if (i != THREAD_NUM - 1) {
                blockList.add(new Block(i * blockSize, (i + 1) * blockSize - 1));
            } else {
                blockList.add(new Block(i * blockSize, this.taskLength));
            }
        }
    }

    @Override
    public void run() {
        downloadStatus = DownloadStatus.PROGRESS;
        es = Executors.newFixedThreadPool(THREAD_NUM);

        // No iterator
        for (int i = 0; i < THREAD_NUM; i++) {
            DownloadThread dt = new DownloadThread(blockList.get(i));
            downloadThreads.add(dt);
            es.submit(dt);
        }
        es.shutdown();

        System.out.println("下载文件：" + task.getDownloadLink());
        while (true) {
            downloadedLength = 0;
            for (Block block : blockList) {
                downloadedLength += block.downloadedSize;
            }

            pb.show(downloadedLength, taskLength);

            if (es.isTerminated()) {
                break;
            }

            if (downloadStatus == DownloadStatus.PAUSE) {
                pb.show(downloadedLength, taskLength);
                System.out.println();
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        downloadStatus = DownloadStatus.FINISH;
        System.out.println("文件：" + task.getTargetFilePath() + " 下载完成！");
    }

    public void setThreadNum(int threadNum) {
        this.THREAD_NUM = threadNum;
    }

    public void pauseDownload() {
        downloadStatus = DownloadStatus.PAUSE;
        for (int i = 0; i < THREAD_NUM; i++) {
            downloadThreads.get(i).setCancelFlag(true);
            Block block = blockList.get(i);
            block.startPos += block.downloadedSize - 1;
        }
    }

    public void resumeDownload() {
        new Thread(this).start();
    }

    public void startDownload() {
        new Thread(this).start();
    }

    public enum DownloadStatus {
        FINISH,
        PAUSE,
        PROGRESS
    }

    public class Block {
        private long startPos;
        private long endPos;
        private long downloadedSize;
        private String downloadLink = task.getDownloadLink();
        private String targetFilePath = task.getTargetFilePath();

        public Block(long startPos, long endPos) {
            this.startPos = startPos;
            this.endPos = endPos;
        }

        public long getStartPos() {
            return startPos;
        }

        public long getEndPos() {
            return endPos;
        }

        public String getDownloadLink() {
            return downloadLink;
        }

        public String getTargetFilePath() {
            return targetFilePath;
        }

        public void setDownloadedSize(long downloadedSize, boolean isSum) {
            if (isSum) {
                this.downloadedSize = downloadedSize;
            } else {
                this.downloadedSize += downloadedSize;
            }
        }

        @Override
        public String toString() {
            return "Block{" +
                    "startPos=" + startPos +
                    ", endPos=" + endPos +
                    ", downloadedSize=" + downloadedSize +
                    '}';
        }
    }
}
