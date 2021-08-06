package com.microyu;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Task implements Serializable {
    private String downloadLink;
    private Path targetFilePath;
    private String targetFileName;
    private long taskLength = -1L;
    private URL url;

    public Task(String downloadLink, String targetFilePath) {
        this.downloadLink = downloadLink;
        this.targetFilePath = Paths.get(new File(targetFilePath).getAbsolutePath());

        try {
            this.url = new URL(this.downloadLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String[] split = url.getFile().split("/");
        targetFileName = split[split.length - 1];
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getTargetFilePath() {
        return targetFilePath.resolve(targetFileName).toString();
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public long getTaskLength() throws IOException {
        if (taskLength == -1L) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            taskLength = conn.getContentLengthLong();
            conn.disconnect();
        }
        return taskLength;
    }
}
