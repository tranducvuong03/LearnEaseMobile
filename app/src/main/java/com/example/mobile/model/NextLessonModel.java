package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class NextLessonModel {
    @SerializedName("lessonType")
    private String lessonType;
    @SerializedName("lessonId")
    private UUID lessonId;
    @SerializedName("promptOrWord")
    private String promptOrWord;
    @SerializedName("audioUrl")
    private String audioUrl;
    @SerializedName("meaning")
    private String meaning;
    @SerializedName("dialectId")
    private UUID dialectId;
    @SerializedName("distractorsJson")
    private String distractorsJson;

    public NextLessonModel() { }

    public String getLessonType() { return lessonType; }
    public void setLessonType(String lessonType) { this.lessonType = lessonType; }
    public UUID getLessonId() { return lessonId; }
    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }
    public String getPromptOrWord() { return promptOrWord; }
    public void setPromptOrWord(String promptOrWord) { this.promptOrWord = promptOrWord; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public UUID getDialectId() { return dialectId; }
    public void setDialectId(UUID dialectId) { this.dialectId = dialectId; }

    public String getDistractorsJson() { return distractorsJson; }
    public void setDistractorsJson(String distractorsJson) { this.distractorsJson = distractorsJson; }
}
