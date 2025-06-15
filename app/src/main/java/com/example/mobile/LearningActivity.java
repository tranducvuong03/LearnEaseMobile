package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.ApiService;
import com.example.mobile.model.NextLessonModel;
// Import các Activity mục tiêu của bạn
import com.example.mobile.QuizActivity;
import com.example.mobile.SpeakingLessonActivity;
import com.example.mobile.TheoryLessonActivity; // Có thể cần thêm
import com.example.mobile.VocabularyLessonActivity; // Có thể cần thêm

import com.example.mobile.utils.RetrofitUtils; // <-- THÊM DÒNG NÀY ĐỂ IMPORT RetrofitUtils

import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
// okhttp3.OkHttpClient; // Không cần import trực tiếp nữa nếu dùng RetrofitUtils
// okhttp3.logging.HttpLoggingInterceptor; // Không cần import trực tiếp nữa nếu dùng RetrofitUtils

public class LearningActivity extends AppCompatActivity {

    private ApiService apiService;
    private final UUID CURRENT_USER_ID = UUID.fromString("A0000000-0000-0000-0000-000000000001");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        // --- SỬ DỤNG PHƯƠNG THỨC "KHÔNG AN TOÀN" CHO MỤC ĐÍCH TEST ---
        Retrofit retrofit = RetrofitUtils.getUnsafeRetrofitInstance(); // <-- Dòng thay đổi chính

        apiService = retrofit.create(ApiService.class);

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
                } else {
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
            intent = new Intent(LearningActivity.this, QuizActivity.class);
            intent.putExtra("VOCAB_ID", nextLesson.getLessonId().toString());
            intent.putExtra("WORD", nextLesson.getPromptOrWord());
            intent.putExtra("MEANING", nextLesson.getMeaning());
            intent.putExtra("AUDIO_URL", nextLesson.getAudioUrl());
            intent.putExtra("DIALECT_ID", nextLesson.getDialectId().toString());
        } else if ("Theory".equalsIgnoreCase(nextLesson.getLessonType())) { // Thêm nếu bạn có TheoryLessonActivity
            intent = new Intent(LearningActivity.this, TheoryLessonActivity.class);
            intent.putExtra("LESSON_ID", nextLesson.getLessonId().toString());
            intent.putExtra("TITLE", nextLesson.getPromptOrWord());
            // Thêm các dữ liệu khác nếu cần
        } else {
            Toast.makeText(LearningActivity.this, "Unknown lesson type: " + nextLesson.getLessonType(), Toast.LENGTH_SHORT).show();
            Log.e("LearningActivity", "Received unknown lesson type: " + nextLesson.getLessonType());
            return;
        }
        startActivity(intent);
    }
}