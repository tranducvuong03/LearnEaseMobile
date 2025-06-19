package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.ApiService;
import com.example.mobile.model.NextLessonModel;
import com.example.mobile.utils.RetrofitClient; // Sử dụng RetrofitClient mới

// Import các Activity mục tiêu của bạn
import com.example.mobile.QuizActivity;          // Đảm bảo import này
import com.example.mobile.SpeakingLessonActivity; // Đảm bảo import này
import com.example.mobile.TheoryLessonActivity;   // Đảm bảo import này

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        // Lấy JWT token từ SharedPreferences.
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPref.getString("auth_token", null);

        if (jwtToken == null || jwtToken.isEmpty()) {
            // Nếu không có token, người dùng chưa đăng nhập hoặc token đã hết hạn
            Toast.makeText(this, "Bạn cần đăng nhập để tiếp tục!", Toast.LENGTH_LONG).show();
            // Điều hướng về màn hình đăng nhập
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Phương thức này sẽ tự động lấy token từ SharedPreferences và thêm vào header Authorization.
        apiService = RetrofitClient.getProtectedApiService(this);

        findViewById(R.id.learnNowButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNextLesson();
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchNextLesson() {
        apiService.getNextLessonForUser().enqueue(new Callback<NextLessonModel>() {
            @Override
            public void onResponse(Call<NextLessonModel> call, Response<NextLessonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NextLessonModel nextLesson = response.body();
                    Log.d("LearningActivity", "Next Lesson: " + nextLesson.getLessonType() + " - " + nextLesson.getPromptOrWord());
                    startLessonActivity(nextLesson);
                } else if (response.code() == 401) {
                    Toast.makeText(LearningActivity.this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                    // Điều hướng về màn hình đăng nhập và xóa các Activity khác khỏi stack
                    Intent loginIntent = new Intent(LearningActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                } else if (response.code() == 404) {
                    Toast.makeText(LearningActivity.this, "Không còn bài học nào hoặc bạn đã hoàn thành tất cả các bài.", Toast.LENGTH_LONG).show();
                    Log.d("LearningActivity", "No more lessons available (HTTP 404).");
                }
                else {
                    String errorMessage = "Failed to fetch next lesson. HTTP Code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            errorMessage += " Details: " + errorBody;
                            Log.e("LearningActivity", "API Error Body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("LearningActivity", "Error reading error body", e);
                    }
                    Log.e("LearningActivity", errorMessage);
                    Toast.makeText(LearningActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NextLessonModel> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e("LearningActivity", errorMessage, t);
                Toast.makeText(LearningActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLessonActivity(NextLessonModel nextLesson) {
        Intent intent;
        if ("Speaking".equalsIgnoreCase(nextLesson.getLessonType())) {
            intent = new Intent(LearningActivity.this, SpeakingLessonActivity.class);
            intent.putExtra("EXERCISE_ID", nextLesson.getLessonId().toString());
            intent.putExtra("PROMPT", nextLesson.getPromptOrWord());
            intent.putExtra("SAMPLE_AUDIO_URL", nextLesson.getAudioUrl());
            intent.putExtra("DIALECT_ID", nextLesson.getDialectId().toString());
        } else if ("Vocabulary".equalsIgnoreCase(nextLesson.getLessonType())) {
            intent = new Intent(LearningActivity.this, QuizActivity.class); // Giả sử QuizActivity xử lý Vocabulary
            intent.putExtra("VOCAB_ID", nextLesson.getLessonId().toString());
            intent.putExtra("WORD", nextLesson.getPromptOrWord());
            intent.putExtra("MEANING", nextLesson.getMeaning());
            intent.putExtra("AUDIO_URL", nextLesson.getAudioUrl());
            intent.putExtra("DIALECT_ID", nextLesson.getDialectId().toString());
        } else if ("Theory".equalsIgnoreCase(nextLesson.getLessonType())) {
            intent = new Intent(LearningActivity.this, TheoryLessonActivity.class);
            intent.putExtra("LESSON_ID", nextLesson.getLessonId().toString());
            intent.putExtra("TITLE", nextLesson.getPromptOrWord());
            // Thêm các dữ liệu khác nếu cần cho bài Theory
        } else {
            Toast.makeText(LearningActivity.this, "Unknown lesson type: " + nextLesson.getLessonType(), Toast.LENGTH_SHORT).show();
            Log.e("LearningActivity", "Received unknown lesson type: " + nextLesson.getLessonType());
            return;
        }
        startActivity(intent);
    }
}