package com.example.mobile; // Đảm bảo đúng package của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class AchievementsActivity extends AppCompatActivity {

    private ProgressBar progressBarAchievements;
    private TextView tvProgressPercentage;
    // Bạn có thể thêm các TextView khác nếu muốn cập nhật nội dung động

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đặt layout cho Activity này
        setContentView(R.layout.activity_achievements); // Đảm bảo tên file layout của bạn là activity_achievements.xml
        LottieAnimationView lottie = findViewById(R.id.lottieComingSoon);
        TextView message = findViewById(R.id.comingSoonText);
        lottie.setAlpha(0f);
        message.setAlpha(0f);

        lottie.animate().alpha(1f).setDuration(700).start();
        message.animate().alpha(1f).setStartDelay(400).setDuration(600).start();
        // Ánh xạ các view từ layout
        ImageButton btnBack = findViewById(R.id.btn_back_achievements);
        ImageButton btnMenu = findViewById(R.id.btn_menu_achievements);
        progressBarAchievements = findViewById(R.id.progress_bar_achievements);
        tvProgressPercentage = findViewById(R.id.tv_progress_percentage);

        // Thiết lập Listener cho nút quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed(); // Quay lại Activity trước đó
                }
            });
        }

        // Thiết lập Listener cho nút menu (nếu có chức năng)
        if (btnMenu != null) {
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Implement menu functionality, e.g., show a popup menu
                    // Toast.makeText(AchievementsActivity.this, "Menu clicked!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Cập nhật tiến độ giả lập (ví dụ, 80%)
        updateAchievementProgress(80);

        // TODO: Bạn có thể thêm logic để tải dữ liệu thành tích từ database hoặc API
        // và cập nhật các thẻ thành tích một cách động nếu cần.
        // Hiện tại, các thẻ đang được hardcode trong XML.
        Button btnBackComingSoon = findViewById(R.id.btnBackComingSoon);
        btnBackComingSoon.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Cập nhật ProgressBar và TextView hiển thị phần trăm tiến độ.
     * @param progress Giá trị tiến độ (từ 0 đến 100).
     */
    private void updateAchievementProgress(int progress) {
        if (progressBarAchievements != null) {
            progressBarAchievements.setProgress(progress);
        }
        if (tvProgressPercentage != null) {
            tvProgressPercentage.setText(progress + "%");
        }
    }
}