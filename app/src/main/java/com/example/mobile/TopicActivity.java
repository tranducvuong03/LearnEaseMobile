package com.example.mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.mobile.adapter.TopicAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.api.TopicAPI;
import com.example.mobile.dialog.NoHeartDialogFragment;
import com.example.mobile.model.HeartResponse;
import com.example.mobile.model.Topic;
import com.example.mobile.service.HeartService;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TopicAdapter topicAdapter;
    private TextView textHeartCount;
    private ImageView heartInfinity;
    private boolean isPremiumToDialog;
    private int heart;
    private List<Topic> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_new);

        recyclerView = findViewById(R.id.recyclerViewTopics);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicList = new ArrayList<>();
        topicAdapter = new TopicAdapter(this, topicList);
        recyclerView.setAdapter(topicAdapter);

        textHeartCount = findViewById(R.id.heartCount);
        heartInfinity = findViewById(R.id.heartInfinity);

        SharedPreferences sp = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sp.getString("user_id", null);

        HeartService.getCurrentHearts(this, userId, new HeartService.FullHeartCallback() {
            @Override
            public void onSuccess(int heartCount, boolean isPremium, int minutesUntilNextHeart) {
                if (isPremium) {
                    textHeartCount.setVisibility(View.GONE);
                    heartInfinity.setVisibility(View.VISIBLE);
                    isPremiumToDialog = true;
                } else {
                    textHeartCount.setVisibility(View.VISIBLE);
                    heartInfinity.setVisibility(View.GONE);
                    textHeartCount.setText(String.valueOf(heartCount));
                    isPremiumToDialog = false;
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(TopicActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        topicAdapter.setOnTopicClickListener(topic -> {
            if (userId == null) {
                relogin();
                return;
            }

            if (isPremiumToDialog) {
                Intent intent = new Intent(TopicActivity.this, TopicLessonsActivity.class);
                intent.putExtra("topic_id", topic.getTopicId());
                intent.putExtra("topic_name", topic.getTitle());
                startActivity(intent);
            } else {
                if (heart <= 0) {
                    NoHeartDialogFragment.show(getSupportFragmentManager());
                } else {
                    // Có tim → cho vào
                    Intent intent = new Intent(TopicActivity.this, TopicLessonsActivity.class);
                    intent.putExtra("topic_id", topic.getTopicId());
                    intent.putExtra("topic_name", topic.getTitle());
                    startActivity(intent);
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(TopicActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.menu_lesson) {
                    return true;
                } else if (id == R.id.menu_challenge) {
                    startActivity(new Intent(TopicActivity.this, ChallengeWeekActivity.class));
                    return true;
                } else if (id == R.id.menu_explore) {
                    startActivity(new Intent(TopicActivity.this, ExploreActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.menu_profile) {
                    startActivity(new Intent(TopicActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_lesson);


        fetchTopicProgressAndHeartFromAPI();
    }

    private void fetchTopicProgressAndHeartFromAPI() {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", null);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this,
                    "User ID not found. Please log in again!",
                    Toast.LENGTH_LONG).show();
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
                    Log.e("TopicActivity", "Error loading topics: code=" + response.code());
                    try {
                        String errorBody = response.errorBody() != null
                                ? response.errorBody().string() : "null";
                        Log.e("TopicActivity", "errorBody = " + errorBody);
                    } catch (Exception e) {
                        Log.e("TopicActivity", "Failed to parse errorBody: " + e.getMessage());
                    }

                    Toast.makeText(TopicActivity.this,
                            "Failed to load topic list!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.e("TopicActivity", "API Error: " + t.getMessage());
                Toast.makeText(TopicActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    private void relogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
