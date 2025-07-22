package com.example.mobile.model;

import java.io.Serializable;

public class SpeakingDialect implements Serializable {
    private String prompt;
    private String audioUrl;

    // Getter
    public String getPrompt() {
        return prompt;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    // Setter
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
