package com.example.mobile; // Đảm bảo package này khớp với package của dự án bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout; // Để bắt sự kiện click cho các category
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PlayTogetherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeplay); // Đảm bảo file layout là activity_play_together.xml

        // --- Back Button Setup ---
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed(); // Quay lại Activity trước đó
                }
            });
        }
        Toast.makeText(this, "PlayTogetherActivity created. Add IDs to categories for click functionality.", Toast.LENGTH_LONG).show();
    }
}