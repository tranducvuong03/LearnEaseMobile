package com.example.mobile.api;

import com.example.mobile.model.EvaluateSpeakingResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CompareSpeakingAPI {
//    String BASE_URL = "https://10.0.2.2:7083/api/";
    String BASE_URL = "https://learnease.id.vn/api/";

    /**
     * Submit user's speaking result to update progress
     * POST /learning/submit-progress?userId={userId}
     */
    @Multipart
    @POST("speaking-ai/Speaking-score-challenge")
    Call<EvaluateSpeakingResponse> evaluateSpeaking(
            @Part MultipartBody.Part AudioFile,
            @Part("OriginalPrompt") RequestBody originalPrompt
    );
    /**
     * Response from compareWithUrl
     */
    class SimilarityRes {
        public float similarity;
    }
}
