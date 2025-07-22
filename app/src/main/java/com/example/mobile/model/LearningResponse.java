// com.example.mobile.model.LearningResponse.java
package com.example.mobile.model;

import java.util.List;
import java.util.UUID;

public class LearningResponse {
    private UUID lessonId;
    private String title;
    private List<VocabularyItem> vocabularies;
    private List<SpeakingExercise> speakingExercises;

    private int vocabCorrectCount;
    private int speakingCorrectCount;

    public UUID getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public List<VocabularyItem> getVocabularies() {
        return vocabularies;
    }

    public List<SpeakingExercise> getSpeakingExercises() {
        return speakingExercises;
    }

    public int getVocabCorrectCount() {
        return vocabCorrectCount;
    }

    public int getSpeakingCorrectCount() {
        return speakingCorrectCount;
    }
}
