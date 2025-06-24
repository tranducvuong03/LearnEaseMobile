package com.example.mobile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;

public class SubscriptionActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private final int ANIMATION_DELAY = 8000;
    private final int CHAR_DELAY = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        ImageView btnBack = findViewById(R.id.btn_back_subscription);
        btnBack.setOnClickListener(v -> onBackPressed());

        String titleText = "Nâng cấp để mở khóa trải nghiệm học tập tuyệt vời!";
        FlexboxLayout container = findViewById(R.id.titleContainer);

        // Tạo từng chữ
        TextView[] letterViews = new TextView[titleText.length()];
        for (int i = 0; i < titleText.length(); i++) {
            TextView letterView = new TextView(this);
            letterView.setText(String.valueOf(titleText.charAt(i)));
            letterView.setTextSize(24);
            letterView.setTypeface(Typeface.DEFAULT_BOLD);
            letterView.setAlpha(0f);
            letterView.setIncludeFontPadding(false);
            container.addView(letterView);
            letterViews[i] = letterView;
        }

        // Animation sau khi layout xong
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runLetterAnimation(letterViews);
            }
        });
    }

    private void runLetterAnimation(TextView[] letterViews) {
        Runnable animate = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < letterViews.length; i++) {
                    TextView letter = letterViews[i];
                    letter.setAlpha(0f);
                    letter.setTranslationY(-100f);

                    // Gradient
                    Shader shader = new LinearGradient(
                            0, 0, letter.getWidth(), 0,
                            new int[]{Color.parseColor("#FF9800"), Color.parseColor("#FFC107"), Color.parseColor("#FF5722")},
                            null,
                            Shader.TileMode.CLAMP
                    );
                    letter.getPaint().setShader(shader);

                    ObjectAnimator drop = ObjectAnimator.ofFloat(letter, "translationY", -100f, 0f);
                    ObjectAnimator fade = ObjectAnimator.ofFloat(letter, "alpha", 0f, 1f);
                    drop.setStartDelay(i * CHAR_DELAY);
                    fade.setStartDelay(i * CHAR_DELAY);
                    drop.setDuration(400);
                    fade.setDuration(400);
                    drop.setInterpolator(new BounceInterpolator());

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(drop, fade);
                    set.start();
                }

                // Lặp lại sau ANIMATION_DELAY
                handler.postDelayed(this, ANIMATION_DELAY);
            }
        };

        handler.post(animate);
    }
}
