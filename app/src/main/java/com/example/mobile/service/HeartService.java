// service/HeartService.java
package com.example.mobile.service;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.HeartResponse;
import com.example.mobile.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartService {
    private static LoginAPI apiService;
    public interface HeartCallback {
        void onSuccess(int heartCount);
        void onFailure(String errorMessage);
    }

    public static void getCurrentHearts(Context context, String userId, HeartCallback callback) {
        apiService = RetrofitClient.getApiService(context);

        apiService.getCurrentHearts(userId).enqueue(new Callback<HeartResponse>() {
            @Override
            public void onResponse(@NonNull Call<HeartResponse> call, @NonNull Response<HeartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getHearts());
                } else {
                    String message = "Lỗi lấy tim: " + response.code();
                    Log.e("HeartService", message);
                    callback.onFailure(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HeartResponse> call, @NonNull Throwable t) {
                String error = "Lỗi mạng: " + t.getMessage();
                Log.e("HeartService", error, t);
                callback.onFailure(error);
            }
        });
    }
    public static void useHearts(Context context, String userId, HeartCallback callback) {
        apiService = RetrofitClient.getApiService(context);

        apiService.useHearts(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(-1); // Giảm tim thành công
                } else {
                    callback.onFailure("Không thể trừ tim. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onFailure("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
