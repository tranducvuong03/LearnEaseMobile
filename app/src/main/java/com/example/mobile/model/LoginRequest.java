package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username") // Đảm bảo khớp với tên thuộc tính JSON trên backend
    private String username;

    @SerializedName("password") // Đảm bảo khớp với tên thuộc tính JSON trên backend
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter và Setter (tùy chọn, Gson không yêu cầu setter nếu bạn chỉ gửi đi)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}