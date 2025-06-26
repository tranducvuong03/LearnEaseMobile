package com.example.mobile;

public enum SkillType {
    Listening,
    Speaking,
    Reading,
    Writing;

    public static SkillType fromString(String value) {


        switch (value.toLowerCase()) {
            case "listening":
                return Listening;
            case "speaking":
                return Speaking;
            case "reading":
                return Reading;
            case "writing":
                return Writing;
            default:
                throw new IllegalArgumentException("Unknown skill type: " + value);
        }
    }
}
