package com.example.mobile.model;
//hướng dẫn đáp án của quiz reading
public class ExplanationItem {
    private String questionNumber;
    private String question;
    private String correct;
    private String userAnswer;
    private String explanation;

    public String getQuestionNumber() { return questionNumber; }
    public String getQuestion() { return question; }
    public String getCorrect() { return correct; }
    public String getUserAnswer() { return userAnswer; }
    public String getExplanation() { return explanation; }
}
