package com.example.mobile.model;

public class ChallengeWeek {
    private String shortName;
    private ChallengeStatus status;  // ✔ Đặt tên rõ ràng hơn

    public ChallengeWeek(String shortName, ChallengeStatus status) {
        this.shortName = shortName;
        this.status = status;
    }

    // Getter
    public String getShortName() {
        return shortName;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    // Setter
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }
}

