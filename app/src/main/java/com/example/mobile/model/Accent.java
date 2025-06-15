package com.example.mobile.model;

public class Accent {
    private String name;
    private String country;

    public Accent(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() { return name; }
    public String getCountry() { return country; }
}
