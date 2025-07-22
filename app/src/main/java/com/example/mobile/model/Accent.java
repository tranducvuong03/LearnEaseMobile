package com.example.mobile.model;

public class Accent {
    public String dialectId;
    public String name;
    public String region;
    public String voiceSampleUrl;
    public Accent(String name, String region) {
        this.name = name;
        this.region = region;
    }

    public String getName() { return name; }
    public String getRegion() { return region; }
}
