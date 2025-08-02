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

    public String getWord() { return word; }

    public String getDistractorsJson() { return distractorsJson; }
}
