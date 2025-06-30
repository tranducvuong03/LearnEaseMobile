package com.example.mobile.model;

public class Message {
    private String text;
    private boolean isUser;

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
