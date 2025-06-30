package com.example.mobile.model;

public class Topic {
    private String title;
    private int progress;
    private String statusText;
    private int total;

    public Topic(String title, int progress, String statusText, int total) {
        this.title = title;
        this.progress = progress;
        this.statusText = statusText;
        this.total = total;
    }

    public String getTitle() { return title; }
    public int getProgress() { return progress; }
    public String getStatusText() { return statusText; }
    public int getTotal() { return total; }
}
