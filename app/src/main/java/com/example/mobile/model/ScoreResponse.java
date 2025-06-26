package com.example.mobile.model;

public class ScoreResponse {
    private float score;
    private String feedback;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

}
