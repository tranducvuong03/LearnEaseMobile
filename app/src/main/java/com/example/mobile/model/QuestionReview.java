package com.example.mobile.model;

import java.util.List;

public class QuestionReview {
    private String question;
    private List<String> choices;
    private String correct;
    private String userAnswer;

    public String getQuestion() { return question; }
    public List<String> getChoices() { return choices; }
    public String getCorrect() { return correct; }
    public String getUserAnswer() { return userAnswer; }
}
