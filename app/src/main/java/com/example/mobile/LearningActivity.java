package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;

import com.example.mobile.api.LessonAPI;
import com.example.mobile.model.LearningResponse;
import com.example.mobile.service.HeartService;
import com.example.mobile.utils.RetrofitClient;
import com.example.mobile.utils.UsagePrefs;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningActivity extends AppCompatActivity {

    private String lessonId, topicId;
    private TextView titleText;
    private ImageView backButton;
    private MaterialButton learnNowButton;
    private TextView textHeartCount;
    private LearningResponse lessonData;
    private long startTime;

    @UnstableApi
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        textHeartCount = findViewById(R.id.heartCount);
        ImageView heartInfinity = findViewById(R.id.heartInfinity);

        SharedPreferences sp = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sp.getString("user_id", null);

        HeartService.getCurrentHearts(this, userId, new HeartService.FullHeartCallback() {
            @Override
            public void onSuccess(int heartCount, boolean isPremium, int minutesUntilNextHeart) {
                if (isPremium) {
                    textHeartCount.setVisibility(View.GONE);
                    heartInfinity.setVisibility(View.VISIBLE);
                } else {
                    textHeartCount.setVisibility(View.VISIBLE);
                    heartInfinity.setVisibility(View.GONE);
                    textHeartCount.setText(String.valueOf(heartCount));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(LearningActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // 1. Lấy dữ liệu từ Intent
        lessonId = getIntent().getStringExtra("lesson_id");
        topicId = getIntent().getStringExtra("topic_id");

        if (lessonId == null || topicId == null) {
            Toast.makeText(this, "Thiếu dữ liệu bài học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ánh xạ view
        titleText = findViewById(R.id.titleText);
        backButton = findViewById(R.id.backButton);
        learnNowButton = findViewById(R.id.learnNowButton);

        // 3. Gọi API để load dữ liệu
        loadLessonInfo();

        // 4. Sự kiện nút back
        backButton.setOnClickListener(v -> finish());

        // 5. Sự kiện "Learn Now"
        learnNowButton.setOnClickListener(v -> {
            if (lessonData == null) {
                Toast.makeText(this, "Dữ liệu chưa sẵn sàng", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LearningActivity.this, QuizActivity.class);
            intent.putExtra("lesson_title", lessonData.getTitle());
            intent.putExtra("lesson_id", lessonData.getLessonId().toString());

            // Chuyển dữ liệu list qua dạng JSON
            Gson gson = new Gson();
            String vocabJson = gson.toJson(lessonData.getVocabularies());
            String speakingJson = gson.toJson(lessonData.getSpeakingExercises());

            intent.putExtra("vocab_list", vocabJson);
            intent.putExtra("speaking_list", speakingJson);

            startActivity(intent);
        });
    }

    // Khi nhấn nút back sẽ load lại
    @Override
    protected void onResume() {
        super.onResume();
        loadLessonInfo();
        startTime = System.currentTimeMillis(); // bắt đầu tính
    }

    private void loadLessonInfo() {
        // Lấy userId từ SharedPreferences
        String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        LessonAPI api = RetrofitClient.getRetrofit().create(LessonAPI.class);
        api.getLessonBlock(userId, lessonId).enqueue(new Callback<LearningResponse>() {
            @Override
            public void onResponse(Call<LearningResponse> call, Response<LearningResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessonData = response.body();

                    // Set tiêu đề bài học
                    titleText.setText(lessonData.getTitle());
                    lessonId = lessonData.getLessonId().toString(); // Cập nhật lại ID nếu cần

                    // 1. Vocabulary progress
                    int vocabCorrect = lessonData.getVocabCorrectCount();
                    int vocabTotal = lessonData.getVocabularies().size();
                    int vocabPercent = (vocabTotal > 0) ? (vocabCorrect * 100 / vocabTotal) : 0;

                    ProgressBar vocabProgressBar = findViewById(R.id.progressBar);
                    TextView vocabProgressText = findViewById(R.id.progressText);
                    vocabProgressBar.setProgress(vocabPercent);
                    vocabProgressText.setText(vocabCorrect + "/" + vocabTotal);

                    // 2. Speaking progress
                    int speakingCorrect = lessonData.getSpeakingCorrectCount();
                    int speakingTotal = lessonData.getSpeakingExercises().size();
                    int speakingPercent = (speakingTotal > 0) ? (speakingCorrect * 100 / speakingTotal) : 0;

                    ProgressBar speakingProgressBar = findViewById(R.id.progressBarGreen);
                    TextView speakingProgressText = findViewById(R.id.progressTextSpeaking);
                    speakingProgressBar.setProgress(speakingPercent);
                    speakingProgressText.setText(speakingCorrect + "/" + speakingTotal);

                } else {
                    Toast.makeText(LearningActivity.this, "Không tìm thấy bài học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LearningResponse> call, Throwable t) {
                Toast.makeText(LearningActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        long sessionTime = System.currentTimeMillis() - startTime;
        UsagePrefs.saveUsageTime(this, sessionTime); // lưu thời gian dùng
    }
}
