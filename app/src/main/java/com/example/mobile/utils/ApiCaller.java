package com.example.mobile.utils;

import android.app.Activity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCaller {
    public static <T> void callWithLoading(Activity activity, Call<T> call, Callback<T> callback) {
        // Hiển thị dialog loading
        LoadingManager.getInstance().show(activity);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                LoadingManager.getInstance().dismiss();
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                LoadingManager.getInstance().dismiss();
                callback.onFailure(call, t);
            }
        });
    }
}
