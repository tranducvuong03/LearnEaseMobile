package com.example.mobile.model;

import java.util.List;

public class ReviewResponse {
    private String skill;
    private String prompt;
    private float score;
    private String feedback;
    private String userAnswer;
    private String referenceText;
    private String audioUrl;
    private String attemptedAt;
    private List<QuestionReview> questions;

    public String getSkill() { return skill; }
    public String getPrompt() { return prompt; }
    public float getScore() { return score; }
    public String getFeedback() { return feedback; }
    public String getUserAnswer() { return userAnswer; }
    public String getReferenceText() { return referenceText; }
    public String getAudioUrl() { return audioUrl; }
    public String  getAttemptedAt() { return attemptedAt; }
    public List<QuestionReview> getQuestions() { return questions; }
}
