package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ActivitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("user_name", null);
        // --- Header and Profile Info Setup ---
        ImageView btnBack = findViewById(R.id.btn_back_activities);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        TextView tvUserName = findViewById(R.id.tv_user_name_activities);

        if (tvUserName != null) {
            tvUserName.setText(username);
        }

        TextView tvHeartsCount = findViewById(R.id.tv_hearts_count);
        // if (tvHeartsCount != null) { tvHeartsCount.setText("4"); }

        TextView tvDiamondsCount = findViewById(R.id.tv_diamonds_count);
        // if (tvDiamondsCount != null) { tvDiamondsCount.setText("40"); }

        /*// --- Activity Cards Setup with Click Listeners ---
        CardView cardPronunciationTest = findViewById(R.id.card_pronunciation_test);
        if (cardPronunciationTest != null) {
            cardPronunciationTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivitiesActivity.this, PronunciationTestActivity.class));
                    Toast.makeText(ActivitiesActivity.this, "Chuyển đến Kiểm tra phát âm", Toast.LENGTH_SHORT).show();
                }
            });
        }

        CardView cardWordGame = findViewById(R.id.card_word_game);
        if (cardWordGame != null) {
            cardWordGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivitiesActivity.this, WordGameActivity.class));
                    Toast.makeText(ActivitiesActivity.this, "Chuyển đến Trò chơi từ vựng", Toast.LENGTH_SHORT).show();
                }
            });
        }*/

        CardView cardShop = findViewById(R.id.card_shop);
        if (cardShop != null) {
            cardShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivitiesActivity.this, ShopActivity.class));
                }
            });
        }

        CardView cardPlayTogether = findViewById(R.id.card_play_together);
        if (cardPlayTogether != null) {
            cardPlayTogether.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivitiesActivity.this, PlayTogetherActivity.class));
                }
            });
        }
    }
}