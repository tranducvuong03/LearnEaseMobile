package com.example.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SkillPrefs {
    private static final String PREF = "SkillDonePrefs";

    private static String key(String userId, String lessonId, String skill) {
        return userId + "_" + lessonId + "_" + skill.toLowerCase(Locale.ROOT);
    }

    private static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Đánh dấu skill đã làm xong trong NGÀY hiện tại
    public static void setDoneToday(Context ctx, String userId, String lessonId, String skill) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key(userId, lessonId, skill), today()).apply();
    }

    // Skill này đã done hôm nay chưa?
    public static boolean isDoneToday(Context ctx, String userId, String lessonId, String skill) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String savedDate = sp.getString(key(userId, lessonId, skill), null);
        return today().equals(savedDate);
    }
}
