package com.example.mobile.api;

import com.example.mobile.model.EvaluateLessonRequest;
import com.example.mobile.model.ExplanationResponse;
import com.example.mobile.model.LessonResponse;
import com.example.mobile.model.ReviewResponse;
import com.example.mobile.model.ScoreResponse;
import com.example.mobile.model.GoogleLoginRequest;
import com.example.mobile.model.LessonModel;
import com.example.mobile.model.CheckoutResponse;
import com.example.mobile.model.LoginRequest;
import com.example.mobile.model.LoginResponse;
import com.example.mobile.model.NextLessonModel;
import com.example.mobile.model.SubscriptionInfo;
import com.example.mobile.model.TranscriptionResponse;
import com.example.mobile.model.VocabularyItem;
import com.example.mobile.model.SpeakingExercise;
import com.example.mobile.model.userData.UpdateAvatarRequest;
import com.example.mobile.model.userData.UpdateUsernameRequest;
import com.example.mobile.model.userData.UserResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginAPI {
    String BASE_URL = "https://10.0.2.2:7083/api/";
    //    String BASE_URL = "http://14.225.218.235/api/";
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/google-login")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);
    @GET("users/me")
    Call<UserResponse> getMyProfile(@Header("Authorization") String token);

    @PUT("users/{id}/username")
    Call<Void> updateUserNameById(@Path("id") String userId,
                                  @Body UpdateUsernameRequest request);
    @POST("/ai/ask")
    Call<String> askAI(@Body Map<String, String> json);

    @PUT("users/{id}/avatar")
    Call<Void> updateAvatarById(@Path("id") String userId,
                                  @Body UpdateAvatarRequest request);
    @GET("VocabularyItems")
    Call<List<VocabularyItem>> getAllVocabularyItems();

    @GET("VocabularyItems/{id}")
    Call<VocabularyItem> getVocabularyItemById(@Path("id") UUID id);

    @GET("SpeakingExercises")
    Call<List<SpeakingExercise>> getAllSpeakingExercises();

    @GET("SpeakingExercises/{id}")
    Call<SpeakingExercise> getSpeakingExerciseById(@Path("id") UUID id);

    @GET("learning/next-lesson-block")
    Call<LessonModel> getNextLessonBlock(@Header("Authorization") String token);

    @GET("learning/next-lesson")
    Call<NextLessonModel> getNextLessonForUser(@Header("Authorization") String token);

    @GET("Learning/next-lesson")
    Call<NextLessonModel> getNextLessonForUser();

    //----------------subscription------------------------//
    @POST("/api/subscription/pay")
    Call<CheckoutResponse> createSubscription(@Body Map<String, String> planType);
    @GET("/api/subscription/me")
    Call<SubscriptionInfo> getMySubscription();
    // 🧠 AI Lesson - Weekly
    @GET("/api/ai-lesson/weekly")
    Call<LessonResponse> getLessonOfTheWeek();

    // 📊 Review kết quả
    @GET("/api/ai-lesson/review")
    Call<ReviewResponse> reviewLesson(
            @Query("userId") UUID userId,
            @Query("lessonId") UUID lessonId,
            @Query("skill") String skill
    );

    // 📘 Giải thích kết quả
    @GET("/api/ai-lesson/explanation")
    Call<ExplanationResponse> getExplanations(
            @Query("userId") UUID userId,
            @Query("lessonId") UUID lessonId,
            @Query("skill") String skill
    );

    //  Gửi bài Reading/Listening
    @POST("/api/ai-lesson/evaluate")
    Call<ScoreResponse> evaluateLesson(@Body EvaluateLessonRequest request);

    // ✍ Gửi bài Writing
    @POST("/api/ai-lesson/evaluate-writing")
    Call<ScoreResponse> evaluateWritingAnswer(@Body EvaluateLessonRequest request);

    //  Gửi bài Speaking (ghi âm)
    @Multipart
    @POST("/api/speech/evaluate")
    Call<ScoreResponse> evaluateSpeaking(
            @Part MultipartBody.Part AudioFile,
            @Part("Prompt") RequestBody prompt
    );
    //cham diem listen
    @POST("/api/ai-lesson/evaluate-listening")
    Call<ScoreResponse> evaluateListening(@Body EvaluateLessonRequest request);
    //cham diem speaking
    @Multipart
    @POST("speech/transcribe")
    Call<TranscriptionResponse> transcribeAudio(
            @Part MultipartBody.Part audioFile
    );

}