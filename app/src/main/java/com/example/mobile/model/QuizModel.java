package com.example.mobile.model;

public class QuizModel {
    private String questionText;
    private int imageResId;
    private String[] options;
    private int correctIndex;

    public QuizModel(String questionText, int imageResId, String[] options, int correctIndex) {
        this.questionText = questionText;
        this.imageResId = imageResId;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getQuestionText() { return questionText; }
    public int getImageResId() { return imageResId; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
}
