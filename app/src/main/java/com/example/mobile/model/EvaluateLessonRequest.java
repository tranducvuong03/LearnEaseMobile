package com.example.mobile.model;

import java.util.Map;
import java.util.UUID;

public class EvaluateLessonRequest {
    private UUID userId;
    private UUID lessonId;
    private String skill;
    private Map<String, String> answers;

    public EvaluateLessonRequest(UUID userId, UUID lessonId, String skill, Map<String, String> answers) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.skill = skill;
        this.answers = answers;
    }

    public UUID getUserId() { return userId; }
    public UUID getLessonId() { return lessonId; }
    public String getSkill() { return skill; }
    public Map<String, String> getAnswers() { return answers; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }
    public void setSkill(String skill) { this.skill = skill; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
}
