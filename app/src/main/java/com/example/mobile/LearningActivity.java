package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.TopicAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.LessonModel;
import com.example.mobile.model.Topic;
import com.example.mobile.model.VocabularyItem;
import com.example.mobile.model.SpeakingExercise;
import com.example.mobile.utils.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningActivity extends AppCompatActivity {

//    private static final int REQ_VOCAB = 1001;
//    private static final int REQ_SPEAKING = 1002;
//
//    private LoginAPI loginAPI;
//    private List<VocabularyItem> vocabList;
//    private List<SpeakingExercise> speakingList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_learning);
//
//        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
//        String jwt = sp.getString("auth_token", null);
//        if (jwt == null || jwt.isEmpty()) {
//            Toast.makeText(this, "Bạn cần đăng nhập để tiếp tục!", Toast.LENGTH_LONG).show();
//            startActivity(new Intent(this, LoginActivity.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//            finish();
//            return;
//        }
//
//        loginAPI = RetrofitClient.getApiService(this);
//        findViewById(R.id.learnNowButton).setOnClickListener(v -> fetchNextLesson());
//        findViewById(R.id.backButton).setOnClickListener(v -> finish());
//    }
//
//    private void fetchNextLesson() {
//        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String token = prefs.getString("auth_token", null);
//
//        if (token == null || token.isEmpty()) {
//            relogin();
//            return;
//        }
//
//        loginAPI.getNextLessonBlock("Bearer " + token).enqueue(new Callback<LessonModel>() {
//            @Override
//            public void onResponse(Call<LessonModel> call, Response<LessonModel> res) {
//                if (res.isSuccessful() && res.body() != null) {
//                    vocabList = res.body().getVocabularies();
//                    speakingList = res.body().getSpeakingExercises();
//                    startVocabPart();
//                } else if (res.code() == 404) {
//                    Toast.makeText(LearningActivity.this,
//                            "Bạn đã hoàn thành tất cả bài học!", Toast.LENGTH_LONG).show();
//                } else if (res.code() == 401) {
//                    relogin();
//                } else {
//                    showError("Fail " + res.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LessonModel> call, Throwable t) {
//                showError("Network: " + t.getMessage());
//            }
//        });
//    }
//
//    private void startVocabPart() {
//        Intent intent = new Intent(this, QuizActivity.class);
//        intent.putExtra("QUESTIONS_JSON", new Gson().toJson(vocabList));
//        startActivityForResult(intent, REQ_VOCAB);
//    }
//
//    private void startSpeakingPart() {
//        Intent intent = new Intent(this, SpeakingLessonActivity.class);
//        intent.putExtra("SPEAKING_LIST", new Gson().toJson(speakingList));
//        startActivityForResult(intent, REQ_SPEAKING);
//    }
//
//    @Override
//    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {
//        super.onActivityResult(reqCode, resCode, data);
//        if (resCode != RESULT_OK) return;
//
//        if (reqCode == REQ_VOCAB) {
//            startSpeakingPart();
//        } else if (reqCode == REQ_SPEAKING) {
//            fetchNextLesson(); // cả lesson đã hoàn thành
//        }
//    }
//
//    private void relogin() {
//        Toast.makeText(this, "Phiên đăng nhập hết hạn!", Toast.LENGTH_LONG).show();
//        startActivity(new Intent(this, LoginActivity.class)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        finish();
//    }
//
//    private void showError(String msg) {
//        Log.e("LearningActivity", msg);
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//    }

    private RecyclerView recyclerView;
    private TopicAdapter topicAdapter;
    private List<Topic> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_new);

        recyclerView = findViewById(R.id.recyclerViewTopics);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        topicList = new ArrayList<>();
        topicList.add(new Topic("Topic 1", 80, "Completed", 40));
        topicList.add(new Topic("Topic 2", 50, "In Progress", 25));
        topicList.add(new Topic("Topic 3", 100, "Completed", 10));

        topicAdapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(topicAdapter);
    }

}
