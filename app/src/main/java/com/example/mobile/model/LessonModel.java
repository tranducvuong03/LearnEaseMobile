package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

public class LessonModel {
    @SerializedName("lessonId")
    private UUID lessonId;

    @SerializedName("title")
    private String title;

    @SerializedName("vocabularies")
    private List<VocabularyItem> vocabularies;

    @SerializedName("speakingExercises")
    private List<SpeakingExercise> speakingExercises;

    public UUID getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public List<VocabularyItem> getVocabularies() { return vocabularies; }
    public List<SpeakingExercise> getSpeakingExercises() { return speakingExercises; }

    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }
    public void setTitle(String title) { this.title = title; }
    public void setVocabularies(List<VocabularyItem> vocabularies) { this.vocabularies = vocabularies; }
    public void setSpeakingExercises(List<SpeakingExercise> speakingExercises) { this.speakingExercises = speakingExercises; }
}
