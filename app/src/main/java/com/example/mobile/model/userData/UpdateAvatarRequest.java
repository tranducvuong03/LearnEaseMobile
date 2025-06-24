package com.example.mobile.model.userData;

public class UpdateAvatarRequest {
    private final String avatarUrl;

    public UpdateAvatarRequest(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
