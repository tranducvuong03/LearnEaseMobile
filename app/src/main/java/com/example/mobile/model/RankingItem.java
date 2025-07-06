package com.example.mobile.model;

public class RankingItem {
    private String leaderboardId;
    private String userId;
    private String username;
    private String avatarUrl;
    private String period;         // "weekly" hoặc "monthly"
    private int score;
    private String recordedAt;

    public RankingItem(String leaderboardId, String userId, String username, String avatarUrl, String period, int score, String recordedAt) {
        this.leaderboardId = leaderboardId;
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.period = period;
        this.score = score;
        this.recordedAt = recordedAt;
    }

    // Getters
    public String getLeaderboardId() { return leaderboardId; }
    public String getUserId() { return userId; }
    public String getName() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getPeriod() { return period; }
    public int getScore() { return score; }
    public String getRecordedAt() { return recordedAt; }

    // Setters
    public void setLeaderboardId(String leaderboardId) { this.leaderboardId = leaderboardId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.username = username; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setPeriod(String period) { this.period = period; }
    public void setScore(int score) { this.score = score; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }
}
