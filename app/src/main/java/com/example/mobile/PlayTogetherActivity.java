package com.example.mobile; // Đảm bảo package này khớp với package của dự án bạn

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class PlayTogetherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeplay); // Đảm bảo file layout là activity_play_together.xml
        LottieAnimationView lottie = findViewById(R.id.lottieComingSoon);
        TextView message = findViewById(R.id.comingSoonText);

        lottie.setAlpha(0f);
        message.setAlpha(0f);

        lottie.animate().alpha(1f).setDuration(700).start();
        message.animate().alpha(1f).setStartDelay(400).setDuration(600).start();

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
        Button btnBackComingSoon = findViewById(R.id.btnBackComingSoon);
        btnBackComingSoon.setOnClickListener(v -> {
            finish();
        });
//        Toast.makeText(this, "PlayTogetherActivity created. Add IDs to categories for click functionality.", Toast.LENGTH_LONG).show();
    }
}