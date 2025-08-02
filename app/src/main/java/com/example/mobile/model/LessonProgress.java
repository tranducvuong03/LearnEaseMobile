package com.example.mobile.model;

public class LessonProgress {
    private String lessonId;
    private int vocabCorrect;
    private int speakingCorrect;
    private boolean isCompleted;

    public LessonProgress(String lessonId) {
        this.lessonId = lessonId;
        this.vocabCorrect = 0;
        this.speakingCorrect = 0;
        this.isCompleted = false;
    }

    // Getters
    public String getLessonId() {
        return lessonId;
    }

    public int getVocabCorrect() {
        return vocabCorrect;
    }

    public int getSpeakingCorrect() {
        return speakingCorrect;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int getTotalCorrect() {
        return vocabCorrect + speakingCorrect;
    }

    // Setters
    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public void setVocabCorrect(int vocabCorrect) {
        this.vocabCorrect = vocabCorrect;
        checkCompletion();
    }

    public void setSpeakingCorrect(int speakingCorrect) {
        this.speakingCorrect = speakingCorrect;
        checkCompletion();
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Update completion based on total score
    public void checkCompletion() {
        isCompleted = getTotalCorrect() >= 8;
    }
}
