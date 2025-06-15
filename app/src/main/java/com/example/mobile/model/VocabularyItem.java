package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID; // Dùng java.util.UUID cho Guid

public class VocabularyItem {
    @SerializedName("vocabId")
    private UUID vocabId;
    @SerializedName("dialectId")
    private UUID dialectId;
    @SerializedName("word")
    private String word;
    @SerializedName("meaning")
    private String meaning;
    @SerializedName("audioUrl")
    private String audioUrl;

    // Constructors, getters, setters
    public UUID getVocabId() { return vocabId; }
    public void setVocabId(UUID vocabId) { this.vocabId = vocabId; }
    public UUID getDialectId() { return dialectId; }
    public void setDialectId(UUID dialectId) { this.dialectId = dialectId; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}
