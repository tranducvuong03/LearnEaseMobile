package com.example.mobile;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.ExplanationItem;
import com.example.mobile.model.ExplanationResponse;
import com.example.mobile.model.ReviewResponse;
import com.example.mobile.utils.LoadingManager;
import com.example.mobile.utils.RetrofitClient;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewResultActivity extends AppCompatActivity {
    private TextView textSkill,  textScore, textFeedback, textTime;
    private LoginAPI apiService;
    Button buttonShowExplanation;
    private TextView textReference;

    LinearLayout explanationContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_result);

        textSkill = findViewById(R.id.textSkill);

        textScore = findViewById(R.id.textScore);
        textFeedback = findViewById(R.id.textFeedback);
        buttonShowExplanation = findViewById(R.id.buttonShowExplanation);
        explanationContainer = findViewById(R.id.explanationContainer);



        UUID userId = UUID.fromString(getIntent().getStringExtra("userId"));
        UUID lessonId = UUID.fromString(getIntent().getStringExtra("lessonId"));
        SkillType skill = SkillType.valueOf(getIntent().getStringExtra("skill"));
        buttonShowExplanation.setOnClickListener(v -> {
            buttonShowExplanation.setEnabled(false); // disable sau khi bấm
            explanationContainer.setVisibility(View.VISIBLE);
            fetchExplanation(userId, lessonId, skill);
        });
        fetchReview(userId, lessonId, skill);
    }
    private void fetchExplanation(UUID userId, UUID lessonId, SkillType skill) {
        LoginAPI api = RetrofitClient.getApiService(this);

        // ✅ Hiện loading trước khi gửi API
        LoadingManager.getInstance().show(ReviewResultActivity.this);

        api.getExplanations(userId, lessonId, skill.name()).enqueue(new Callback<ExplanationResponse>() {
            @Override
            public void onResponse(Call<ExplanationResponse> call, Response<ExplanationResponse> response) {
                LoadingManager.getInstance().dismiss(); // ✅ Ẩn loading

                if (response.isSuccessful() && response.body() != null) {
                    for (ExplanationItem item : response.body().getExplanations()) {
                        addExplanationToUI(item);
                    }
                } else {
                    Toast.makeText(ReviewResultActivity.this, "Lỗi tải giải thích.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExplanationResponse> call, Throwable t) {
                LoadingManager.getInstance().dismiss(); // ✅ Ẩn luôn khi lỗi
                Toast.makeText(ReviewResultActivity.this, "API lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addExplanationToUI(ExplanationItem item) {
        TextView text = new TextView(this);
        String number = item.getQuestionNumber() != null ? item.getQuestionNumber() : "";
        String question = item.getQuestion() != null ? item.getQuestion() : "";
        String correct = item.getCorrect() != null ? item.getCorrect() : "?";
        String userAnswer = item.getUserAnswer() != null ? item.getUserAnswer() : "?";
        String explanation = item.getExplanation() != null ? item.getExplanation() : "Không có giải thích.";

        text.setText(
                number + "\n\n" +
                        "❓ " + question + "\n" +
                        "✔ Đáp án đúng: " + correct + " | ❌ Bạn chọn: " + userAnswer + "\n\n" +
                        "📘 " + explanation
        );

        text.setPadding(0, 12, 0, 12);
        text.setTextColor(Color.parseColor("#444444"));
        text.setTextSize(15f);

        explanationContainer.addView(text);
    }
    private void fetchReview(UUID userId, UUID lessonId, SkillType skill) {
        LoginAPI api = RetrofitClient.getApiService(this);
        api.reviewLesson(userId, lessonId, skill.name()).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse data = response.body();
                    textSkill.setText(  data.getSkill());

                    textScore.setText("Score: " + data.getScore());
                    textFeedback.setText("Feedback:\n" + data.getFeedback());

                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                textFeedback.setText("❌ Lỗi khi tải kết quả: " + t.getMessage());
                t.printStackTrace(); // hoặc Log.e(...) nếu cần
            }
        });
    }
}
