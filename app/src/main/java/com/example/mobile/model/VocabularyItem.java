package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

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

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("distractorsJson")
    private String distractorsJson;

    // Getters and setters
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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDistractorsJson() { return distractorsJson; }
    public void setDistractorsJson(String distractorsJson) { this.distractorsJson = distractorsJson; }
}
