package com.example.mobile.model;

import java.util.List;

public class LessonDetailResponse {
    public String lessonId;
    public String topic;
    public String createdAt;
    public List<Part> parts;

    public static class Part {
        public String skill;
        public String prompt;
        public String referenceText;
        public String audioUrl;
        public String choicesJson;
    }
}
