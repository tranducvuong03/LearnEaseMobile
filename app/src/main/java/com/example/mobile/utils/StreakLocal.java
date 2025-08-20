package com.example.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StreakLocal {
    private static final String PREF = "MyAppPrefs";
    private static final String KEY = "streak_credited_date";

    public static boolean hasCreditedToday(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String creditedDate = prefs.getString(KEY, "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return today.equals(creditedDate);
    }
}
