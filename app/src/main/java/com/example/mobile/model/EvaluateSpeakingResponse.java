package com.example.mobile.model;

public class EvaluateSpeakingResponse {
    private String prompt;
    private String transcript;
    private int score;
    private String feedback;

    public EvaluateSpeakingResponse() {
    }

    public String getPromt() {
        return prompt;
    }

    public String getTranscript() {
        return transcript;
    }

    public int getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }
}