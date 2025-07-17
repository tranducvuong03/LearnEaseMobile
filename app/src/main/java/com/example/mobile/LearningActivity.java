package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.mobile.adapter.TopicAdapter;
import com.example.mobile.api.TopicAPI;
import com.example.mobile.model.Topic;
import com.example.mobile.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningActivity extends AppCompatActivity {

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
        topicAdapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(topicAdapter);

        fetchTopicProgressFromAPI();
    }

    private void fetchTopicProgressFromAPI() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", null);
        String token = sp.getString("auth_token", null);

        // Nếu user_id bị thiếu thì decode từ token
        if ((userId == null || userId.isEmpty()) && token != null) {
            try {
                JWT jwt = new JWT(token);
                userId = jwt.getClaim("nameid").asString();
                // Lưu lại để lần sau dùng
                sp.edit().putString("user_id", userId).apply();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Không giải mã được token!", Toast.LENGTH_LONG).show();
                relogin();
                return;
            }
        }

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy userId, vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            relogin();
            return;
        }

        TopicAPI topicAPI = RetrofitClient.getRetrofit().create(TopicAPI.class);
        topicAPI.getTopicsWithProgress(userId).enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topicList.clear();
                    topicList.addAll(response.body());
                    topicAdapter.notifyDataSetChanged();
                } else {
                    // In thêm thông tin lỗi
                    Log.e("LearningActivity", "Lỗi tải topic: code=" + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("LearningActivity", "errorBody = " + errorBody);
                    } catch (Exception e) {
                        Log.e("LearningActivity", "errorBody parse lỗi: " + e.getMessage());
                    }

                    Toast.makeText(LearningActivity.this, "Lỗi tải danh sách topic!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.e("LearningActivity", "API Error: " + t.getMessage());
                Toast.makeText(LearningActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void relogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
