package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.StreakResponse;
import com.example.mobile.utils.RetrofitClient;
import com.example.mobile.utils.UsagePrefs;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        String username = prefs.getString("user_name", null);
        updateDailyProgress();
        // Greeting text
        TextView greetingText = findViewById(R.id.greeting);
        String greeting = "Nice to see you, " + username + " !";
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
                    startActivity(new Intent(HomeActivity.this, RankingActivity.class));
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
        LottieAnimationView chatBubble = findViewById(R.id.chatBotAnimation);
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
                        tvStreak.setText(streak + " days");
                        ImageView staticFire = findViewById(R.id.staticFireIcon);
                        LottieAnimationView lottieFire = findViewById(R.id.lottieFireStreak);
                        LinearLayout cardStreak = findViewById(R.id.card_streak);
                        if (streak >= 1) {
                            staticFire.setVisibility(View.GONE);
                            lottieFire.setVisibility(View.VISIBLE);
                            lottieFire.playAnimation();
                            cardStreak.setBackgroundResource(R.drawable.bg_orange_card);
                        } else {
                            lottieFire.setVisibility(View.GONE);
                            lottieFire.cancelAnimation();
                            staticFire.setVisibility(View.VISIBLE);
                            cardStreak.setBackgroundResource(R.drawable.bg_grey_card);
                        }

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
    private void updateDailyProgress() {

        // Mục tiêu: 25 phút mỗi ngày
        int goalMillis = 25 * 60 * 1000;

        // Lấy thời gian đã học hôm nay
        long usedMillis = UsagePrefs.getTodayUsage(this);
        float usedMinutes = usedMillis / 60000f;
        int percent = (int) ((usedMillis * 100f) / goalMillis);
        if (percent > 100) percent = 100;

        // Tìm và cập nhật view
        LottieAnimationView fireAnim = findViewById(R.id.fireProgressAnimation);
        TextView timeText = findViewById(R.id.textViewTime);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        DonutProgress donut = findViewById(R.id.progressCircle);
        int usedMinutesInt = (int) (usedMillis / 60000);
        timeText.setText(String.format(Locale.getDefault(), "%d / 25 mins", usedMinutesInt));
        progressBar.setProgress(percent);
        donut.setProgress(percent);
        donut.setText(percent + "%");
        if (percent >= 100) {
            progressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.progress_done_gradient));
            donut.setFinishedStrokeColor(ContextCompat.getColor(this, R.color.green));
            fireAnim.setVisibility(View.VISIBLE);
            fireAnim.playAnimation();
            if (!hasShownToday()) {
                showCongratsOverlay();  // Show overlay
                markOverlayAsShown();
            }
        }else {
            fireAnim.setVisibility(View.GONE);
            fireAnim.cancelAnimation();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateDailyProgress();
    }
    private void showCongratsOverlay() {
        FrameLayout overlay = findViewById(R.id.congrats_overlay);
        LottieAnimationView lottie = findViewById(R.id.lottieCongrats);
        Button btnClose = findViewById(R.id.btnCloseCongrats);

        overlay.setVisibility(View.VISIBLE);
        lottie.playAnimation();

        btnClose.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
        });
    }
    private boolean hasShownToday() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String lastShownDate = prefs.getString("overlay_shown_date", "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return today.equals(lastShownDate);
    }

    private void markOverlayAsShown() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        prefs.edit().putString("overlay_shown_date", today).apply();
    }

}