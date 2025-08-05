package com.example.mobile.api;

import com.example.mobile.model.SubmitProgressRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LearningAPI {
//    String BASE_URL = "https://10.0.2.2:7083/api/";
    String BASE_URL = "https://learnease.id.vn/api/";
    @POST("learning/submit-progress")
    Call<Void> submitProgress(
            @Query("userId") String userId,
            @Body SubmitProgressRequest request
    );
}
