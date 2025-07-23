package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;

import com.example.mobile.api.LearningAPI;
import com.example.mobile.model.LessonProgress;
import com.example.mobile.model.SubmitProgressRequest;
import com.example.mobile.model.VocabularyItem;
import com.example.mobile.utils.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@UnstableApi
public class QuizActivity extends AppCompatActivity {

    private List<VocabularyItem> questions;
    private int currentIndex = 0;
    private int correctAnswers = 0;

    private TextView questionText;
    private ImageView questionImage;

    private LinearLayout initialAnswerLayout;
    private LinearLayout correctFeedbackLayout;
    private LinearLayout wrongFeedbackLayout;
    private TextView correctFeedbackAnswer;
    private TextView wrongFeedbackTitle;
    private Button correctNextQuestionButton;
    private Button wrongNextQuestionButton;

    private Button[] answerBtns;
    private View[] progressDots;

    private String correctAnswer = "";
    private LessonProgress lessonProgress;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_vocab);

        mapViews();
        mapProgressDots();

        String json = getIntent().getStringExtra("vocab_list");
        String lessonId = getIntent().getStringExtra("lesson_id");
        userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("user_id", null);

        if (json != null) {
            questions = new Gson().fromJson(json, new TypeToken<List<VocabularyItem>>() {
            }.getType());
        } else {
            questions = new ArrayList<>();
        }

        lessonProgress = new LessonProgress(lessonId);

        showQuestion();

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void mapViews() {
        questionText = findViewById(R.id.questionText);
        questionImage = findViewById(R.id.questionImage);
        initialAnswerLayout = findViewById(R.id.initialAnswerLayout);

        correctFeedbackLayout = findViewById(R.id.correctFeedbackLayout);
        wrongFeedbackLayout = findViewById(R.id.wrongFeedbackLayout);
        correctFeedbackAnswer = findViewById(R.id.correctFeedbackAnswer);
        correctNextQuestionButton = findViewById(R.id.correctNextQuestionButton);
        wrongFeedbackTitle = findViewById(R.id.wrongFeedbackTitle);
        wrongNextQuestionButton = findViewById(R.id.wrongNextQuestionButton);

        answerBtns = new Button[]{
                findViewById(R.id.buttonMouth),
                findViewById(R.id.buttonEyes),
                findViewById(R.id.buttonEar),
                findViewById(R.id.buttonNose)
        };

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            answerBtns[i].setOnClickListener(v -> checkAnswer(idx));
        }

        correctNextQuestionButton.setOnClickListener(v -> goToNextQuestion());
        wrongNextQuestionButton.setOnClickListener(v -> goToNextQuestion());
    }

    private void mapProgressDots() {
        progressDots = new View[]{
                findViewById(R.id.dot0), findViewById(R.id.dot1), findViewById(R.id.dot2),
                findViewById(R.id.dot3), findViewById(R.id.dot4), findViewById(R.id.dot5),
                findViewById(R.id.dot6), findViewById(R.id.dot7), findViewById(R.id.dot8)
        };
    }

    private void updateProgress(int curIdx) {
        for (int i = 0; i < progressDots.length; i++) {
            progressDots[i].setBackgroundResource(
                    (i <= curIdx) ? R.drawable.blue_dot : R.drawable.gray_dot);
        }
    }

    private void showQuestion() {
        resetView();

        VocabularyItem item = questions.get(currentIndex);
        correctAnswer = item.getWord();
        questionText.setText("Từ n\u00e0o mang nghĩa: " + correctAnswer);

        questionImage.setVisibility(View.GONE);

        List<String> options = parseDistractors(item.getDistractorsJson());
        options.add(correctAnswer);
        Collections.shuffle(options);

        for (int i = 0; i < 4 && i < options.size(); i++) {
            answerBtns[i].setText(options.get(i));
        }

        updateProgress(currentIndex);
    }

    private List<String> parseDistractors(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        return new Gson().fromJson(json, new TypeToken<List<String>>() {
        }.getType());
    }

    private void checkAnswer(int selectedIndex) {
        String selected = answerBtns[selectedIndex].getText().toString();
        boolean isCorrect = selected.equals(correctAnswer);

        initialAnswerLayout.setVisibility(View.GONE);

        VocabularyItem item = questions.get(currentIndex);
        if (isCorrect) {
            correctAnswers++;
            correctFeedbackLayout.setVisibility(View.VISIBLE);
            correctFeedbackAnswer.setText("Answer: " + correctAnswer);
            submitProgressToServer(userId, lessonProgress.getLessonId(), item.getVocabId(), true);
        } else {
            wrongFeedbackLayout.setVisibility(View.VISIBLE);
            // Hiển thị tiêu đề & nút Next
            wrongFeedbackTitle.setText("Oops… that's wrong");
            wrongNextQuestionButton.setVisibility(View.VISIBLE);

            // Gửi kết quả sai
            submitProgressToServer(userId, lessonProgress.getLessonId(), item.getVocabId(), false);
        }

        updateProgress(currentIndex);
    }

    private void submitProgressToServer(String userId, String lessonId, UUID vocabId, boolean isCorrect) {
        SubmitProgressRequest request = new SubmitProgressRequest(lessonId, vocabId, null, isCorrect);

        LearningAPI api = RetrofitClient.getLearningApi(getApplicationContext());
        api.submitProgress(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("SubmitProgress", "Fail: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SubmitProgress", "Network error: " + t.getMessage());
            }
        });
    }

    private void goToNextQuestion() {
        currentIndex++;
        if (currentIndex < questions.size()) {
            showQuestion();
        } else {
            lessonProgress.setVocabCorrect(correctAnswers);
            finishQuiz();
        }
    }

    private void resetView() {
        initialAnswerLayout.setVisibility(View.VISIBLE);
        correctFeedbackLayout.setVisibility(View.GONE);
        wrongFeedbackLayout.setVisibility(View.GONE);
    }

    private void finishQuiz() {
        Intent intent = new Intent(QuizActivity.this, SpeakingLessonActivity.class);
        String speakingJson = getIntent().getStringExtra("speaking_list");
        intent.putExtra("speaking_list", speakingJson);
        intent.putExtra("lesson_progress", new Gson().toJson(lessonProgress));
        // thêm offset = số câu quiz đã đi qua 5
        intent.putExtra("dotOffset", currentIndex);
        startActivity(intent);
        finish();
    }

}
