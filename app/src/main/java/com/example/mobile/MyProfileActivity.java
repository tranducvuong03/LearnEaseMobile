package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class MyProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        TextView logoutTextView = findViewById(R.id.item_logout_account);
        logoutTextView.setOnClickListener(v -> {
            logoutUser();
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MyProfileActivity.this, StarterActivity.class));
                finish();
            }
        }, 1500);
    }

    // ✅ Đặt ngoài onCreate()
    private void logoutUser() {
        // Xoá dữ liệu SharedPreferences
        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Quay về màn hình Login
        Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // xoá backstack
        startActivity(intent);
        finish();
    }
}
