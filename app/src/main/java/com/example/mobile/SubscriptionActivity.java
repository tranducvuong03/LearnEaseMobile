package com.example.mobile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobile.api.ApiService;
import com.example.mobile.model.CheckoutResponse;
import com.example.mobile.model.SubscriptionInfo;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.LoadingManager;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        Button btnMonthly = findViewById(R.id.btn_subscribe_monthly);
        Button btnYearly = findViewById(R.id.btn_subscribe_yearly);

        btnMonthly.setOnClickListener(v -> startPayment("monthly"));
        btnYearly.setOnClickListener(v -> startPayment("yearly"));

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
    //payment
    private void startPayment(String planType) {
        Map<String, String> body = new HashMap<>();
        body.put("planType", planType);

        Call<CheckoutResponse> call = RetrofitClient.getApiService(this).createSubscription(body);

        ApiCaller.callWithLoading(this, call, new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                LoadingManager.getInstance().dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().checkoutUrl;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Toast.makeText(SubscriptionActivity.this, "Không thể tạo thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                LoadingManager.getInstance().dismiss();
                Toast.makeText(SubscriptionActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkSubscriptionStatus();
    }
    private void checkSubscriptionStatus() {
        Call<SubscriptionInfo> call = RetrofitClient.getApiService(this).getMySubscription();

        ApiCaller.callWithLoading(this, call, new Callback<SubscriptionInfo>() {
            @Override
            public void onResponse(Call<SubscriptionInfo> call, Response<SubscriptionInfo> response) {
                LoadingManager.getInstance().dismiss();
                SubscriptionInfo sub = response.body();
                if (response.isSuccessful() && sub != null && sub.isActive()) {
                    updateSubscriptionUI(sub.getPlanType(), sub.getEndDate());
                }
            }

            @Override
            public void onFailure(Call<SubscriptionInfo> call, Throwable t) {
                LoadingManager.getInstance().dismiss();
                // Có thể log hoặc hiển thị trạng thái mặc định
            }
        });
    }
    private void updateSubscriptionUI(String planType, String endDateRaw) {
        if (planType == null || endDateRaw == null) {
            Toast.makeText(this, "Không thể xác định gói đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        Button btnMonth = findViewById(R.id.btn_subscribe_monthly);
        Button btnYear = findViewById(R.id.btn_subscribe_yearly);
        LinearLayout cardMonth = findViewById(R.id.card_monthly);
        LinearLayout cardYear = findViewById(R.id.card_yearly);

        String formattedDate = formatDate(endDateRaw);
        TextView expireText = new TextView(this);
        expireText.setText("Hết hạn: " + formattedDate);
        expireText.setTextColor(Color.parseColor("#E65100"));
        expireText.setTextSize(16f);
        expireText.setTypeface(Typeface.DEFAULT_BOLD);
        expireText.setPadding(0, 16, 0, 0);

        if ("monthly".equalsIgnoreCase(planType)) {
            btnMonth.setEnabled(false);
            btnMonth.setText("Đã đăng ký");
            btnMonth.setBackgroundResource(R.drawable.btn_disabled_gray);
            cardMonth.setBackgroundResource(R.drawable.bg_card_highlight_orange);
            ((ViewGroup) btnMonth.getParent()).addView(expireText);
        } else if ("yearly".equalsIgnoreCase(planType)) {
            btnYear.setEnabled(false);
            btnYear.setText("Đã đăng ký");
            btnYear.setBackgroundResource(R.drawable.btn_disabled_gray);
            cardYear.setBackgroundResource(R.drawable.bg_card_highlight_green);
            ((ViewGroup) btnYear.getParent()).addView(expireText);
        }
    }


    private String formatDate(String isoDateStr) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = isoFormat.parse(isoDateStr);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            return isoDateStr;
        }
    }



}
