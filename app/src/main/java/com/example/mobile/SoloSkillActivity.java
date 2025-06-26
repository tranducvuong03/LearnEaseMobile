package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.ApiService;
import com.example.mobile.model.LessonResponse;

import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoloSkillActivity extends AppCompatActivity {

    private MaterialCardView cardListening, cardSpeaking, cardReading, cardWriting;
    private View skillGrid;
    private LessonResponse lesson;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_skill);

        // Ánh xạ view
        skillGrid = findViewById(R.id.skill_grid);
        cardListening = findViewById(R.id.card_listening);
        cardSpeaking = findViewById(R.id.card_speaking);
        cardReading = findViewById(R.id.card_reading);
        cardWriting = findViewById(R.id.card_writing);

        // Ẩn giao diện kỹ năng trong lúc loading
        skillGrid.setVisibility(View.GONE);

        // Gọi API trước khi hiển thị UI
        apiService = RetrofitClient.getApiService(this);
        fetchInitialData();
    }

    private void fetchInitialData() {
        // Ví dụ: lấy bài học tuần hoặc setting người dùng
        Call<LessonResponse> call = apiService.getLessonOfTheWeek(); // bạn cần định nghĩa API này

        ApiCaller.callWithLoading(this, call, new Callback<LessonResponse>() {
            @Override
            public void onResponse(Call<LessonResponse> call, Response<LessonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lesson = response.body();
                    skillGrid.setVisibility(View.VISIBLE); // 👈 THÊM DÒNG NÀY
                    setupListeners(lesson);
                } else {
                    Toast.makeText(SoloSkillActivity.this, "Failed to load lesson", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LessonResponse> call, Throwable t) {
                Toast.makeText(SoloSkillActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners(LessonResponse lesson) {
        cardListening.setOnClickListener(v -> startSkillActivity("Listening", lesson));
        cardSpeaking.setOnClickListener(v -> startSkillActivity("Speaking", lesson));
        cardReading.setOnClickListener(v -> startSkillActivity("Reading", lesson));
        cardWriting.setOnClickListener(v -> startSkillActivity("Writing", lesson));
    }
    private void startSkillActivity(String skill, LessonResponse lesson) {
        Intent intent = new Intent(SoloSkillActivity.this, SoloQuizActivity.class);
        intent.putExtra("skill", skill);
        intent.putExtra("lesson_json", new Gson().toJson(lesson));
        intent.putExtra("lessonId", lesson.getLessonId().toString());
        Log.d("SOLO_DEBUG", "LessonId: " + lesson.getLessonId());// lesson là LessonResponse từ API
        startActivity(intent);
    }
}
