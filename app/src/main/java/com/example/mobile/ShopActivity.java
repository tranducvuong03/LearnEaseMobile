package com.example.mobile; // Đảm bảo package này khớp với package của dự án bạn

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout; // Để bắt sự kiện click cho các item
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.HeartAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.HeartResponse;
import com.example.mobile.model.userData.UserResponse;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.LoadingManager;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerHearts;
    private HeartAdapter heartAdapter;
    private LoginAPI apiService;
    private static final String TAG = "ShopActivity";
    private String userId;
    private ImageView backButton;
    private TextView nextHeartTimer;
    private LinearLayout regenHeartAlert;
    private LinearLayout regenHeartAlertWhenPremium;
    private LinearLayout heartPremium;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        recyclerHearts = findViewById(R.id.recycler_hearts);
        heartAdapter = new HeartAdapter(this, 0);
        recyclerHearts.setAdapter(heartAdapter);
        recyclerHearts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        apiService = RetrofitClient.getApiService(this);
        backButton = findViewById(R.id.backButton);
        nextHeartTimer = findViewById(R.id.tv_next_heart_timer);
        regenHeartAlert = findViewById(R.id.regenHeartAlert);
        regenHeartAlertWhenPremium = findViewById(R.id.regenHeartAlertWhenPremium);
        heartPremium = findViewById(R.id.heartPremium);


        // Nút quay lại
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token != null) {
            ApiCaller.callWithLoading(this,
                    apiService.getMyProfile("Bearer " + token),
                    new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            LoadingManager.getInstance().dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UserResponse user = response.body();
                                userId = user.getUserId();
                                getCurrentHeartCount(userId);
                            } else {
                                Log.e(TAG, "Failed with code: " + response.code());
                                Toast.makeText(ShopActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            LoadingManager.getInstance().dismiss();
                            Log.e(TAG, "Error: " + t.getMessage());
                            Toast.makeText(ShopActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Intent intent = new Intent(ShopActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void getCurrentHeartCount(String userId) {
        apiService.getCurrentHearts(userId).enqueue(new Callback<HeartResponse>() {
            @Override
            public void onResponse(@NonNull Call<HeartResponse> call, @NonNull Response<HeartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HeartResponse data = response.body();

                    int heartCount = data.getCurrentHearts();
                    updateHeartUI(heartCount);

                    // Gán thời gian hồi tim vào TextView
                    int minutes = data.getMinutesUntilNextHeart();
                    String formatted = HeartAdapter.formatMinutesToTimeText(minutes);
                    nextHeartTimer.setText(formatted);
                    boolean isPremium = data.isPremium();
                    if (isPremium){
                        regenHeartAlert.setVisibility(View.GONE);
                        regenHeartAlertWhenPremium.setVisibility(View.VISIBLE);
                        recyclerHearts.setVisibility(View.GONE);
                        heartPremium.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("HEART", "Lỗi khi lấy tim: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HeartResponse> call, @NonNull Throwable t) {
                Log.e("HEART", "Lỗi mạng", t);
            }
        });
    }


    private void updateHeartUI(int heartCount) {
        heartAdapter.updateHeartCount(heartCount);
    }
}