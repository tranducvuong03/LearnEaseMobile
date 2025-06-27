package com.example.mobile.model.aiChat;

public class AIChatBoxRequest {
    private String userInput;

    public AIChatBoxRequest(String userInput) {
        this.userInput = userInput;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
