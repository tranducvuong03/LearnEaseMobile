package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class EvaluateAccentResponse {

    @SerializedName("dialect")
    private String dialect;

    @SerializedName("transcript")
    private String transcript;

    @SerializedName("score")
    private int score;

    @SerializedName("feedback")
    private String feedback;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
