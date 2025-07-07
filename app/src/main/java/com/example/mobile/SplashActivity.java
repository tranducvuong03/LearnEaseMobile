package com.example.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends Activity {
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, StarterActivity.class));
                finish();
            }
        }, 1500);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Giả lập tác vụ tải dữ liệu trong 2 giây
        new Thread(() -> {
            // Giả lập load dữ liệu
            try {
                Thread.sleep(2000); // giả lập tải dữ liệu
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("auth_token", null);

            // Sau khi tải xong, chuyển Activity
            runOnUiThread(() -> {
                if (token != null && !token.isEmpty()) {
                    // ✅ Đã có token → vào Home
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    // ❌ Chưa có token → vào Starter
                    startActivity(new Intent(SplashActivity.this, StarterActivity.class));
                }
                finish();
            });
        }).start();
    }

} 