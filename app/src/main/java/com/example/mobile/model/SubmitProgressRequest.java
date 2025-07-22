package com.example.mobile.model;

import java.util.UUID;

public class SubmitProgressRequest {
    private String lessonId;
    private UUID vocabId;
    private UUID exerciseId;
    private boolean isCorrect;

    public SubmitProgressRequest(String lessonId, UUID vocabId, UUID exerciseId, boolean isCorrect) {
        this.lessonId = lessonId;
        this.vocabId = vocabId;
        this.exerciseId = exerciseId;
        this.isCorrect = isCorrect;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public UUID getVocabId() {
        return vocabId;
    }

    public void setVocabId(UUID vocabId) {
        this.vocabId = vocabId;
    }

    public UUID getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(UUID exerciseId) {
        this.exerciseId = exerciseId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}