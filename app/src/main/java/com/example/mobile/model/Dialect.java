package com.example.mobile.model;

public class Dialect {
    private String dialectId;
    private String name;
    private String region;
    private String voiceSampleUrl;

    public String getDialectId() {
        return dialectId;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getVoiceSampleUrl() {
        return voiceSampleUrl;
    }

    public void setDialectId(String dialectId) {
        this.dialectId = dialectId;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setVoiceSampleUrl(String voiceSampleUrl) {
        this.voiceSampleUrl = voiceSampleUrl;
    }
}
