package com.example.mobile.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface CompareSpeakingAPI {
    String BASE_URL = "https://10.0.2.2:7083/api/";
//    String BASE_URL = "https://learnease.id.vn/api/";
    @Multipart
    @POST("/compare")
    Call<SimilarityRes> compareWithUrl(
            @Part MultipartBody.Part user_audio,      // file .wav ghi âm
            @Part("sample_url") RequestBody sampleUrl // URL Firebase
    );

    @Multipart
    @POST("/compare")
    Call<SimilarityRes> compareWithFile(
            @Part MultipartBody.Part user_audio,      // file .wav ghi âm
            @Part MultipartBody.Part ref_audio        // file mẫu .wav
    );

    // model nhận JSON {"similarity": 0.87}
    class SimilarityRes { public float similarity; }
}
