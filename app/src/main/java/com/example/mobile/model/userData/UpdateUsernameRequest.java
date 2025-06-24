package com.example.mobile.model.userData;

public class UpdateUsernameRequest {
    private final String username;

    public UpdateUsernameRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
