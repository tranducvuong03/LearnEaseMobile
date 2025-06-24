package com.example.mobile.utils;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    private static boolean isInitialized = false;
    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dixycod0d");
            config.put("api_secret", "wCZxoJhK2vsydT3wrZ4e6ofeGso");
            config.put("use_scheduled_uploads", "false");

            MediaManager.init(context.getApplicationContext(), config);
            isInitialized = true;
        }
    }
}

