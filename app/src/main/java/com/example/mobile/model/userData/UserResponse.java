package com.example.mobile.model.userData;

import com.google.gson.annotations.SerializedName;

    public class UserResponse {
        @SerializedName("userId")
        private String userId;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        @SerializedName("avatarUrl")
        private String avatarUrl;

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        // Optionally: setters nếu bạn muốn cho phép chỉnh sửa
}
