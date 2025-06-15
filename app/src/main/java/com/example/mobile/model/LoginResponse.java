package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token") // Đảm bảo khớp với tên thuộc tính JSON trên backend
    private String token;

    // Getter
    public String getToken() {
        return token;
    }

    // Setter (tùy chọn, nếu bạn muốn Retrofit gán giá trị)
    public void setToken(String token) {
        this.token = token;
    }
}