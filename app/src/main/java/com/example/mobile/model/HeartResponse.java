package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class HeartResponse {
    @SerializedName("currentHearts")
    private int currentHearts;

    @SerializedName("isPremium")
    private boolean isPremium;

    @SerializedName("lastUsedAt")
    private String lastUsedAt; // dùng String hoặc Date tùy bạn xử lý

    @SerializedName("lastRegeneratedAt")
    private String lastRegeneratedAt;

    @SerializedName("minutesUntilNextHeart")
    private int minutesUntilNextHeart;

    public int getCurrentHearts() {
        return currentHearts;
    }

    public void setCurrentHearts(int currentHearts) {
        this.currentHearts = currentHearts;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(String lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public String getLastRegeneratedAt() {
        return lastRegeneratedAt;
    }

    public void setLastRegeneratedAt(String lastRegeneratedAt) {
        this.lastRegeneratedAt = lastRegeneratedAt;
    }

    public int getMinutesUntilNextHeart() {
        return minutesUntilNextHeart;
    }

    public void setMinutesUntilNextHeart(int minutesUntilNextHeart) {
        this.minutesUntilNextHeart = minutesUntilNextHeart;
    }
}
