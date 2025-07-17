package com.example.mobile.model;

public class Topic {
    private String topicId;
    private String title;
    private String description;
    private int totalLessons;
    private int completedLessons;
    private int progressPercent;
    private String status;

    public String getTopicId() { return topicId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTotalLessons() { return totalLessons; }
    public int getCompletedLessons() { return completedLessons; }
    public int getProgressPercent() { return progressPercent; }
    public String getStatus() { return status; }
}
