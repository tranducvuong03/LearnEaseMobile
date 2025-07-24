package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class HeartResponse {
    @SerializedName("hearts")
    private int hearts;

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }
}
