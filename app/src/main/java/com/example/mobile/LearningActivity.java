package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile.api.LessonAPI;
import com.example.mobile.model.Lesson;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningActivity extends AppCompatActivity {

    private String lessonId, topicId;
    private TextView titleText;
    private ImageView backButton;
    private MaterialButton learnNowButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        // Lấy dữ liệu từ Intent
        lessonId = getIntent().getStringExtra("lesson_id");
        topicId = getIntent().getStringExtra("topic_id");

        if (lessonId == null || topicId == null) {
            Toast.makeText(this, "Thiếu dữ liệu bài học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        titleText = findViewById(R.id.titleText);
        backButton = findViewById(R.id.backButton);
        learnNowButton = findViewById(R.id.learnNowButton);

        // Gọi API lấy nội dung bài học
        loadLessonInfo();

        // Sự kiện nút back
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Sự kiện "Learn Now"
        learnNowButton.setOnClickListener(v -> {

        });
    }

    private void loadLessonInfo() {
        LessonAPI api = RetrofitClient.getRetrofit().create(LessonAPI.class);
//        api.getLessonById(UUID.fromString(lessonId)).enqueue(new Callback<Lesson>() {
//            @Override
//            public void onResponse(Call<Lesson> call, Response<Lesson> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Lesson lesson = response.body();
//                    titleText.setText(lesson.getTitle());
//                    // bạn có thể bind thêm các thông tin khác nếu cần
//                } else {
//                    Toast.makeText(LearningActivity.this, "Không tải được bài học", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Lesson> call, Throwable t) {
//                Toast.makeText(LearningActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
