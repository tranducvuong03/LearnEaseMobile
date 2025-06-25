package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class SubscriptionInfo {
    @SerializedName("planType")
    private String planType;

    @SerializedName("price")
    private int price;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("active")
    private boolean active;

    public String getPlanType() {
        return planType;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }
}

