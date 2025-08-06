package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.utils.UsagePrefs;

public class CompletionActivity extends AppCompatActivity {
    private long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_lesson);

        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            // Quay về MainActivity
            Intent intent = new Intent(CompletionActivity.this, TopicActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis(); // bắt đầu tính
    }

    @Override
    protected void onPause() {
        super.onPause();
        long sessionTime = System.currentTimeMillis() - startTime;
        UsagePrefs.saveUsageTime(this, sessionTime); // lưu thời gian dùng
    }
}
