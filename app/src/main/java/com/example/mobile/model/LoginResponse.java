package com.example.mobile.model;

import com.example.mobile.model.userData.UserResponse;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private UserResponse userResponse;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return userResponse;
    }

    public void setUser(UserResponse userResponse) {
        this.userResponse = userResponse;
    }
}
