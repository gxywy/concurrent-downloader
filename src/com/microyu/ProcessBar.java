package com.microyu;

import java.text.DecimalFormat;

public class ProcessBar {
    private int barLen = 50;
    private long lastCurrent = 0;
    private String finishedChar = "█";
    private String unfinishedChar = "-";

    private DecimalFormat processFormater = new DecimalFormat("#.##%");
    private DecimalFormat sizeFormater = new DecimalFormat("#.##");

    public void setBarChar(String finishedChar, String unfinishedChar) {
        this.finishedChar = finishedChar;
        this.unfinishedChar = unfinishedChar;
    }

    public boolean show(long current, long total) {
        reset();

        if (current >= total) {
            draw(barLen, total, total);
            afterComplete();
            return false;
        } else {
            draw(barLen, current, total);
        }
        return true;
    }

    private void draw(int barLen, long current, long total) {
        float rate = (float) (current * 1.0 / total);
        int len = (int) (rate * barLen);

        StringBuilder sb = new StringBuilder();
        sb.append(formatProcess(rate));
        sb.append(" |");
        for (int i = 0; i < len; i++) {
            sb.append(finishedChar);
        }
        for (int i = 0; i < barLen - len; i++) {
            sb.append(unfinishedChar);
        }
        sb.append("| ");
        sb.append("(" + getFormatLength(current) + "/" + getFormatLength(total) + ", ");
        sb.append("Speed: " + getFormatLength(current - lastCurrent) + "/s)");
        lastCurrent = current;
        System.out.print(sb.toString());
    }

    private String getFormatLength(long length) {
        if(length < 1024){
            return "" + formatSize(length) + "B";
        }else if(length < 1024*1024){
            return "" + formatSize(length>>>10) + "KB";
        }else if(length < 1024*1024*1024){
            return "" + formatSize(length>>>20) + "MB";
        }else {
            return "" + formatSize(length>>>30) + "GB";
        }
    }

    private void reset() {
        System.out.print('\r');
    }

    private void afterComplete() {
        System.out.print('\n');
    }

    private String formatProcess(float num) {
        return processFormater.format(num);
    }

    private String formatSize(float num) {
        return sizeFormater.format(num);
    }

    public static void main(String[] args) throws InterruptedException {
        ProcessBar pb = new ProcessBar();
        for (int i = 1; i <= 100; i++) {
            pb.show((long) i, 100L);
            Thread.sleep(100);
        }
    }
}
