package com.example.mobile.view;

public interface QuizView {
    void showQuestion(String questionText, int imageResId, String[] options);
    void showCorrectAnswer(String correctAnswer);
    void showWrongAnswer(String correctAnswer);
    void showFeedback(boolean isCorrect, String correctAnswer);
    void resetView();
}
