package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.LessonResponse;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.RetrofitClient;
import com.example.mobile.utils.SkillPrefs;
import com.example.mobile.utils.UsagePrefs;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoloSkillActivity extends AppCompatActivity {

    private MaterialCardView cardListening, cardSpeaking, cardReading, cardWriting;
    private View skillGrid;
    private LessonResponse lesson;

    private LoginAPI apiService;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiService = RetrofitClient.getApiService(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_skill);

        // Ánh xạ view
        skillGrid = findViewById(R.id.skill_grid);
        cardListening = findViewById(R.id.card_listening);
        cardSpeaking = findViewById(R.id.card_speaking);
        cardReading = findViewById(R.id.card_reading);
        cardWriting = findViewById(R.id.card_writing);

        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SoloSkillActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Đóng ChatActivity để không quay lại khi nhấn nút back hệ thống
            }
        });
        // Ẩn giao diện kỹ năng trong lúc loading
        skillGrid.setVisibility(View.GONE);

        // Gọi API trước khi hiển thị UI
        LoginAPI api = RetrofitClient.getApiService(this);
        fetchInitialData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis(); // bắt đầu tính
    }

    @Override
    protected void onPause() {
        super.onPause();
        long sessionTime = System.currentTimeMillis() - startTime;
        UsagePrefs.saveUsageTime(this, sessionTime); // lưu thời gian dùng
    }
    private void fetchInitialData() {
        // Ví dụ: lấy bài học tuần hoặc setting người dùng
        Call<LessonResponse> call = apiService.getTodayLesson();

        ApiCaller.callWithLoading(this, call, new Callback<LessonResponse>() {
            @Override
            public void onResponse(Call<LessonResponse> call, Response<LessonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getLessonId() == null) {
                        Toast.makeText(SoloSkillActivity.this, "Không có bài học tuần này!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    lesson = response.body();
                    skillGrid.setVisibility(View.VISIBLE);
                    applyLocalDoneFlags(lesson);
                    setupListeners(lesson);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : null;
                        Log.w("SOLO_DEBUG", "Error body: " + errorBody);
                        Toast.makeText(SoloSkillActivity.this, "Server response error: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(SoloSkillActivity.this, "Failed to load lesson", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<LessonResponse> call, Throwable t) {
                Toast.makeText(SoloSkillActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getUserIdFromPrefs() {
        return getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("user_id", "");
    }
    private void setupListeners(LessonResponse lesson) {
        String lessonId = lesson.getLessonId().toString();
        String userId = getUserIdFromPrefs();
        if (!SkillPrefs.isDoneToday(this, userId, lessonId, "listening"))
            cardListening.setOnClickListener(v -> startSkillActivity("Listening", lesson));
        else
            cardListening.setOnClickListener(null);

        if (!SkillPrefs.isDoneToday(this, userId, lessonId, "speaking"))
            cardSpeaking.setOnClickListener(v -> startSkillActivity("Speaking", lesson));
        else
            cardSpeaking.setOnClickListener(null);

        if (!SkillPrefs.isDoneToday(this, userId, lessonId, "reading"))
            cardReading.setOnClickListener(v -> startSkillActivity("Reading", lesson));
        else
            cardReading.setOnClickListener(null);

        if (!SkillPrefs.isDoneToday(this, userId, lessonId, "writing"))
            cardWriting.setOnClickListener(v -> startSkillActivity("Writing", lesson));
        else
            cardWriting.setOnClickListener(null);
    }

    private void startSkillActivity(String skill, LessonResponse lesson) {
        Intent intent = new Intent(SoloSkillActivity.this, SoloQuizActivity.class);
        intent.putExtra("skill", skill);
        intent.putExtra("lesson_json", new Gson().toJson(lesson));
        intent.putExtra("lessonId", lesson.getLessonId().toString());
        Log.d("SOLO_DEBUG", "LessonId: " + lesson.getLessonId());// lesson là LessonResponse từ API
        startActivity(intent);
    }
    private void applyLocalDoneFlags(LessonResponse lesson) {
        String lessonId = lesson.getLessonId().toString();
        String userId = getUserIdFromPrefs();
        setCardState(cardListening,  R.id.status_listening,
                SkillPrefs.isDoneToday(this, userId, lessonId, "listening"));
        setCardState(cardSpeaking,   R.id.status_speaking,
                SkillPrefs.isDoneToday(this, userId, lessonId, "speaking"));
        setCardState(cardReading,    R.id.status_reading,
                SkillPrefs.isDoneToday(this, userId, lessonId, "reading"));
        setCardState(cardWriting,    R.id.status_writing,
                SkillPrefs.isDoneToday(this, userId, lessonId, "writing"));
    }

    private void setCardState(com.google.android.material.card.MaterialCardView card, int chipId, boolean done) {
        if (done) {
            card.setEnabled(false);
            card.setAlpha(0.5f);
            findViewById(chipId).setVisibility(View.VISIBLE);
        } else {
            card.setEnabled(true);
            card.setAlpha(1f);
            findViewById(chipId).setVisibility(View.GONE);
        }
    }

}
