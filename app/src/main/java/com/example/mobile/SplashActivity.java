package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
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

            // Sau khi tải xong, chuyển Activity
            runOnUiThread(() -> {
                startActivity(new Intent(SplashActivity.this, StarterActivity.class));
                finish();
            });
        }).start();
    }

} 