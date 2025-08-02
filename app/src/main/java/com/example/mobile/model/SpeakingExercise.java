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

    public String getPrompt() {
        return prompt;
    }

    public UUID getExerciseId() {
        return exerciseId;
    }

    public String getSampleAudioUrl() {
        return sampleAudioUrl;
    }
}