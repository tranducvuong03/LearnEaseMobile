package com.example.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobile.adapter.RankingMonthlyAdapter;
import com.example.mobile.adapter.RankingWeeklyAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.RankingItem;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMonthly;
    private RecyclerView recyclerViewWeekly;
    private RankingMonthlyAdapter adapterMonthly;
    private RankingWeeklyAdapter adapterWeekly;
    private LoginAPI apiService;
    private boolean hasLoadedWeekly = false;
    private boolean hasLoadedMonthly = false;
    // Top 3 views
    private TextView topOneName, topOnePoint;
    private TextView topTwoName, topTwoPoint;
    private TextView topThreeName, topThreePoint;
    private ImageView topOneAvatar, topTwoAvatar, topThreeAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        apiService = RetrofitClient.getApiService(this);

        recyclerViewMonthly = findViewById(R.id.recyclerRankingMonthly);
        recyclerViewMonthly.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewWeekly = findViewById(R.id.recyclerRankingWeekly);
        recyclerViewWeekly.setLayoutManager(new LinearLayoutManager(this));

        adapterMonthly = new RankingMonthlyAdapter(this, new ArrayList<>());
        recyclerViewMonthly.setAdapter(adapterMonthly);

        adapterWeekly = new RankingWeeklyAdapter(this, new ArrayList<>());
        recyclerViewWeekly.setAdapter(adapterWeekly);

        // Ánh xạ top 3 views
        topOneName = findViewById(R.id.weekly_top1_name);
        topOnePoint = findViewById(R.id.weekly_top1_point);
        topOneAvatar = findViewById(R.id.weekly_top1_avatar);

        topTwoName = findViewById(R.id.weekly_top2_name);
        topTwoPoint = findViewById(R.id.weekly_top2_point);
        topTwoAvatar = findViewById(R.id.weekly_top2_avatar);

        topThreeName = findViewById(R.id.weekly_top3_name);
        topThreePoint = findViewById(R.id.weekly_top3_point);
        topThreeAvatar = findViewById(R.id.weekly_top3_avatar);

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup);
        toggleGroup.check(R.id.btnWeekly);
        ScrollView layoutWeekly = findViewById(R.id.layoutWeekly);
        LinearLayout layoutMonthly = findViewById(R.id.layoutMonthly);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnWeekly) {
                if (!hasLoadedWeekly) {
                    fetchWeeklyRanking();
                    hasLoadedWeekly = true;
                }
                layoutWeekly.setVisibility(View.VISIBLE);
                layoutMonthly.setVisibility(View.GONE);
            } else {
                if (!hasLoadedMonthly) {
                    fetchMonthlyRanking();
                    hasLoadedMonthly = true;
                }
                layoutWeekly.setVisibility(View.GONE);
                layoutMonthly.setVisibility(View.VISIBLE);
            }
        });

        // Mặc định load weekly khi vào
        fetchWeeklyRanking();
    }

    private void fetchWeeklyRanking() {
        Call<List<RankingItem>> call = apiService.getTopWeekly("weekly", 10);

        call.enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RankingItem> list = response.body();

                    // Gán top 1
                    if (list.size() > 0) {
                        RankingItem top1 = list.get(0);
                        topOneName.setText(top1.getName());
                        topOnePoint.setText(top1.getScore() + " points");
                        Glide.with(RankingActivity.this).load(top1.getAvatarUrl()).circleCrop().into(topOneAvatar);
                    }

                    // Gán top 2
                    if (list.size() > 1) {
                        RankingItem top2 = list.get(1);
                        topTwoName.setText(top2.getName());
                        topTwoPoint.setText(top2.getScore() + " points");
                        Glide.with(RankingActivity.this).load(top2.getAvatarUrl()).circleCrop().into(topTwoAvatar);
                    }

                    // Gán top 3
                    if (list.size() > 2) {
                        RankingItem top3 = list.get(2);
                        topThreeName.setText(top3.getName());
                        topThreePoint.setText(top3.getScore() + " points");
                        Glide.with(RankingActivity.this).load(top3.getAvatarUrl()).circleCrop().into(topThreeAvatar);
                    }

                    // Truyền phần còn lại vào adapter
                    List<RankingItem> remaining = list.size() > 3 ? list.subList(3, list.size()) : new ArrayList<>();
                    adapterWeekly.setData(remaining);
                } else {
                    Toast.makeText(RankingActivity.this, "Lỗi dữ liệu từ máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Toast.makeText(RankingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMonthlyRanking() {
        Call<List<RankingItem>> call = apiService.getTopMonthly("monthly", 10);

        call.enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterMonthly.setData(response.body());
                } else {
                    Toast.makeText(RankingActivity.this, "Lỗi dữ liệu từ máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Toast.makeText(RankingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
