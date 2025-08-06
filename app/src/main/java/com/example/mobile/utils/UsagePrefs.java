package com.example.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UsagePrefs {
    private static final String PREF_NAME = "usage_tracker";

    // Lấy userId từ SharedPreferences chung của app
    private static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getString("user_id", "unknown");
    }

    // Lấy ngày hiện tại theo định dạng yyyy-MM-dd
    private static String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Tạo key riêng theo từng user và ngày
    private static String getUsageKey(Context context) {
        String userId = getUserId(context);
        String today = getToday();
        return "usage_" + today + "_" + userId;
    }
    private static String getAllTimeKey(Context context) {
        String userId = getUserId(context);
        return "alltime_" + userId;
    }

    // Lưu thêm thời gian sử dụng cho user hôm nay
    public static void saveUsageTime(Context context, long millis) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        //cộng dồn cho hôm nay
        String key = getUsageKey(context);
        long total = prefs.getLong(key, 0);
        prefs.edit().putLong(key, total + millis).apply();
        // cộng dồn cho all time
        String allTimeKey = getAllTimeKey(context);
        long allTimeTotal = prefs.getLong(allTimeKey, 0);
        prefs.edit().putLong(allTimeKey, allTimeTotal + millis).apply();
    }

    // Lấy tổng thời gian đã dùng hôm nay cho user hiện tại
    public static long getTodayUsage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = getUsageKey(context);
        return prefs.getLong(key, 0);
    }
    // Lấy tổng thời gian đã dùng cho user hiện tại
    public static long getAllTimeUsage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String allTimeKey = getAllTimeKey(context);
        return prefs.getLong(allTimeKey, 0);
    }

}
