package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Lesson {
    @SerializedName("lessonId")
    private UUID lessonId;

    @SerializedName("topicId")
    private UUID topicId;

    @SerializedName("title")
    private String title;

    @SerializedName("order")
    private int order;

    public UUID getLessonId() {
        return lessonId;
    }

    public void setLessonId(UUID lessonId) {
        this.lessonId = lessonId;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
