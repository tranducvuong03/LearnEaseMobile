package com.example.mobile.model;

public class RankingItem {
    private String name;
    private int points;
    private int rank;
    private int avatarResId;

    public RankingItem(String name, int points, int rank, int avatarResId) {
        this.name = name;
        this.points = points;
        this.rank = rank;
        this.avatarResId = avatarResId;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getRank() {
        return rank;
    }

    public int getAvatarResId() {
        return avatarResId;
    }
}
