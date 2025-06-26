package com.example.mobile.model;

import com.example.mobile.model.LessonPart;

import java.util.List;

public class LessonResponse {
    private String lessonId;
    private String topic;
    private String createdAt;
    private List<LessonPart> parts;

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<LessonPart> getParts() {
        return parts;
    }

    public void setParts(List<LessonPart> parts) {
        this.parts = parts;
    }
}
