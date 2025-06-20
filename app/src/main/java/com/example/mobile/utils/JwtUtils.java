package com.example.mobile.utils;

import android.util.Base64;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public final class JwtUtils {
    private JwtUtils() {}
    public static boolean isExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return true;               // JWT không hợp lệ
            byte[] payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);
            long expSeconds = new JSONObject(payloadJson).getLong("exp");
            return expSeconds * 1000L <= System.currentTimeMillis();  // *1000 vì exp là giây
        } catch (Exception e) {                                    // JSON/B64 lỗi → coi như hết hạn
            return true;
        }
    }
}
