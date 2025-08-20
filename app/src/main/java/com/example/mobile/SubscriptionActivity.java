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
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.model.CheckoutResponse;
import com.example.mobile.model.SubscriptionInfo;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.LoadingManager;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private final int ANIMATION_DELAY = 8000;
    private final int CHAR_DELAY = 60;
    private boolean isChecking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        // Nếu MỞ từ deep link -> handleDeepLink sẽ tự gọi API (không loader)
        // Nếu KHÔNG có deep link -> gọi 1 lần có loader
        boolean handled = handleDeepLink(getIntent()); // đổi handleDeepLink trả về boolean
        if (!handled) {
            checkSubscriptionStatus(true); // mở màn bình thường
        }
        ImageView btnBack = findViewById(R.id.btn_back_subscription);
        btnBack.setOnClickListener(v -> onBackPressed());

        String titleText = "Upgrade to unlock great learning experiences!";
        FlexboxLayout container = findViewById(R.id.titleContainer);
        List<TextView> letterViews = new ArrayList<>();
        // Tách theo từ để không bị xuống dòng sai
        String[] words = titleText.split(" ");
        for (String word : words) {
            // Tạo LinearLayout chứa từng chữ cái của 1 từ
            LinearLayout wordLayout = new LinearLayout(this);
            wordLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < word.length(); i++) {
                TextView letterView = new TextView(this);
                letterView.setText(String.valueOf(word.charAt(i)));
                letterView.setTextSize(24);
                letterView.setTypeface(Typeface.DEFAULT_BOLD);
                letterView.setAlpha(0f);
                letterView.setIncludeFontPadding(false);
                wordLayout.addView(letterView);
                letterViews.add(letterView);
            }

            // Thêm khoảng trắng sau mỗi từ
            TextView space = new TextView(this);
            space.setText(" ");
            space.setTextSize(24);
            wordLayout.addView(space);

            // Thêm từ vào container
            container.addView(wordLayout);
        }

        // Khi layout xong, chạy animation
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runLetterAnimation(letterViews.toArray(new TextView[0]));
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

    private void checkSubscriptionStatus() { checkSubscriptionStatus(true); }
    private void checkSubscriptionStatus(boolean showLoading) {
        if (isChecking) return;
        isChecking = true;

        Call<SubscriptionInfo> call = RetrofitClient.getApiService(this).getMySubscription();

        Callback<SubscriptionInfo> cb = new Callback<SubscriptionInfo>() {
            @Override public void onResponse(Call<SubscriptionInfo> c, Response<SubscriptionInfo> r) {
                if (showLoading) LoadingManager.getInstance().dismiss();
                isChecking = false;
                SubscriptionInfo sub = r.body();
                if (r.isSuccessful() && sub != null && sub.isActive()) {
                    updateSubscriptionUI(sub.getPlanType(), sub.getEndDate());
                }
            }
            @Override public void onFailure(Call<SubscriptionInfo> c, Throwable t) {
                if (showLoading) LoadingManager.getInstance().dismiss();
                isChecking = false;
            }
        };

        if (showLoading) {
            ApiCaller.callWithLoading(this, call, cb);
        } else {
            call.enqueue(cb);
        }
    }
    private void updateSubscriptionUI(String planType, String endDateRaw) {
        if (planType == null || endDateRaw == null) {
            Toast.makeText(this, "Không thể xác định gói đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        // View trong layout
        LinearLayout cardMonth = findViewById(R.id.card_monthly);
        LinearLayout cardYear  = findViewById(R.id.card_yearly);
        Button btnMonth        = findViewById(R.id.btn_subscribe_monthly);
        Button btnYear         = findViewById(R.id.btn_subscribe_yearly);

        View activeCard        = findViewById(R.id.card_active_subscription);
        TextView tvActivePlan  = findViewById(R.id.tv_active_plan);
        TextView tvExpireDate  = findViewById(R.id.tv_expire_date);

        // 1) Reset về mặc định (chưa mua)
        activeCard.setVisibility(View.GONE);

        btnMonth.setEnabled(true);
        btnMonth.setText("🚀 Start with 39.000 ₫");
        btnMonth.setBackgroundResource(R.drawable.btn_orange_selector);
        cardMonth.setBackgroundResource(R.drawable.rounded_card_default_monthly);

        btnYear.setEnabled(true);
        btnYear.setText("🎉 Save now – choose a yearly plan");
        btnYear.setBackgroundResource(R.drawable.btn_green_selector);
        cardYear.setBackgroundResource(R.drawable.rounded_card_default_yearly);

        // 2) Áp dụng trạng thái đã đăng ký
        String formattedDate = formatDate(endDateRaw);   // bạn đã có hàm này
        String planLabel = "monthly".equalsIgnoreCase(planType) ? "Monthly Plan" : "Yearly Plan";

        tvActivePlan.setText("Subscribed: " + planLabel);
        tvExpireDate.setText("Expires: " + formattedDate);   // dùng "Expires" thay vì "Expired"

        // Nổi bật card đang dùng + disable đúng nút
        if ("monthly".equalsIgnoreCase(planType)) {
            btnMonth.setEnabled(false);
            btnMonth.setText("Subscribed");
            btnMonth.setBackgroundResource(R.drawable.btn_disabled_gray);
            cardMonth.setBackgroundResource(R.drawable.bg_card_highlight_orange);
        } else {
            btnYear.setEnabled(false);
            btnYear.setText("Subscribed");
            btnYear.setBackgroundResource(R.drawable.btn_disabled_gray);
            cardYear.setBackgroundResource(R.drawable.bg_card_highlight_green);
        }

        // 3) Hiện card tóm tắt (chỉ 1 nơi hiển thị ngày hết hạn)
        activeCard.setVisibility(View.VISIBLE);
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
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent); // sẽ gọi checkSubscriptionStatus(false)
    }

    private boolean handleDeepLink(Intent intent) {
        Uri uri = intent != null ? intent.getData() : null;
        if (uri == null) return false;

        String path = uri.getPath(); // "/success" hoặc "/cancel"
        if ("/success".equals(path)) {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            checkSubscriptionStatus(false); // ⬅️ không loader
            return true;
        } else if ("/cancel".equals(path)) {
            Toast.makeText(this, "Thanh toán đã bị huỷ", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }



}
