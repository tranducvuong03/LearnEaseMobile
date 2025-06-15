package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class SpeakingExercise {
    @SerializedName("exerciseId")
    private UUID exerciseId;
    @SerializedName("dialectId")
    private UUID dialectId;
    @SerializedName("prompt")
    private String prompt;
    @SerializedName("sampleAudioUrl")
    private String sampleAudioUrl;
    @SerializedName("referenceText")
    private String referenceText;

    // Constructors, getters, setters
    public UUID getExerciseId() { return exerciseId; }
    public void setExerciseId(UUID exerciseId) { this.exerciseId = exerciseId; }
    public UUID getDialectId() { return dialectId; }
    public void setDialectId(UUID dialectId) { this.dialectId = dialectId; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getSampleAudioUrl() { return sampleAudioUrl; }
    public void setSampleAudioUrl(String sampleAudioUrl) { this.sampleAudioUrl = sampleAudioUrl; }
    public String getReferenceText() { return referenceText; }
    public void setReferenceText(String referenceText) { this.referenceText = referenceText; }
}