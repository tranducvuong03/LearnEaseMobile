package com.example.mobile.api;

import com.example.mobile.GoogleLoginRequest;
import com.example.mobile.model.LoginRequest;
import com.example.mobile.model.LoginResponse;
import com.example.mobile.model.NextLessonModel;
import com.example.mobile.model.VocabularyItem;
import com.example.mobile.model.SpeakingExercise;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface ApiService {
    String BASE_URL = "https://10.0.2.2:7083/api/";

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/google-login")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);

    @GET("VocabularyItems")
    Call<List<VocabularyItem>> getAllVocabularyItems();

    @GET("VocabularyItems/{id}")
    Call<VocabularyItem> getVocabularyItemById(@Path("id") UUID id);

    @GET("SpeakingExercises")
    Call<List<SpeakingExercise>> getAllSpeakingExercises();

    @GET("SpeakingExercises/{id}")
    Call<SpeakingExercise> getSpeakingExerciseById(@Path("id") UUID id);

    @GET("Learning/next-lesson")
    Call<NextLessonModel> getNextLessonForUser();
}