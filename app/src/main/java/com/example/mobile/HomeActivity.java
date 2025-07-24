package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.StreakResponse;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import retrofit2.Call;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        String username = prefs.getString("user_name", null);

        // Greeting text
        TextView greetingText = findViewById(R.id.greeting);
        String greeting = "Great to see you, " + username + "!";
        greetingText.setText(greeting);

        // Button Continue Learning
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TopicActivity.class);
            startActivity(intent);
            finish();
        });

        if (userId != null) {
            fetchStreak(userId);
        } else {
            Log.e("Streak", "userId is null. Can't fetch streak");
        }
        LinearLayout challengeCard = findViewById(R.id.card_challenge);
        challengeCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SoloSkillActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    // Already on Home
                    return true;
                } else if (id == R.id.menu_lesson) {
                    startActivity(new Intent(HomeActivity.this, TopicActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.menu_challenge) {
                    startActivity(new Intent(HomeActivity.this, ShopActivity.class));
                    return true;
                } else if (id == R.id.menu_explore) {
                    startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.menu_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        // Chat bubble
        FloatingActionButton chatBubble = findViewById(R.id.chatBubble);
        chatBubble.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            int lastAction;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        view.setX(event.getRawX() + dX);
                        view.setY(event.getRawY() + dY);
                        lastAction = MotionEvent.ACTION_MOVE;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            view.performClick();  // ✅ gọi để hợp lệ hoá click

                            // Mở ChatActivity
                            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                            startActivity(intent);
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void fetchStreak(String userId) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        LoginAPI api = RetrofitClient.getApiService(this);

        api.getUserStreak(userId).enqueue(new retrofit2.Callback<StreakResponse>() {
            @Override
            public void onResponse(Call<StreakResponse> call, retrofit2.Response<StreakResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int streak = response.body().getStreak();
                    runOnUiThread(() -> {
                        TextView tvStreak = findViewById(R.id.tv_streak_value);
                        tvStreak.setText(streak + " days 🔥");
                    });
                } else {
                    Log.e("Streak", "Failed to load: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StreakResponse> call, Throwable t) {
                Log.e("Streak", "Network error: " + t.getMessage());
            }
        });
    }
}