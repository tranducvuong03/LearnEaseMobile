package com.example.mobile.api;

import com.example.mobile.model.Dialect;
import com.example.mobile.model.EvaluateAccentResponse;
import com.example.mobile.model.EvaluateLessonRequest;
import com.example.mobile.model.ExplanationResponse;
import com.example.mobile.model.LessonResponse;
import com.example.mobile.model.RankingItem;
import com.example.mobile.model.RegisterRequest;
import com.example.mobile.model.ReviewResponse;
import com.example.mobile.model.ScoreResponse;
import com.example.mobile.model.GoogleLoginRequest;
import com.example.mobile.model.LessonModel;
import com.example.mobile.model.CheckoutResponse;
import com.example.mobile.model.LoginRequest;
import com.example.mobile.model.LoginResponse;
import com.example.mobile.model.NextLessonModel;
import com.example.mobile.model.StreakResponse;
import com.example.mobile.model.SubscriptionInfo;
import com.example.mobile.model.TranscriptionResponse;
import com.example.mobile.model.VocabularyItem;
import com.example.mobile.model.SpeakingExercise;
import com.example.mobile.model.SpeakingDialect;
import com.example.mobile.model.userData.UpdateAvatarRequest;
import com.example.mobile.model.userData.UpdateUsernameRequest;
import com.example.mobile.model.userData.UserResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
     //  String BASE_URL = "https://learnease.id.vn/api/";
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/google-login")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);
    @GET("users/me")
    Call<UserResponse> getMyProfile(@Header("Authorization") String token);
    @POST("auth/register")
    Call<ResponseBody> register(@Body RegisterRequest request);
    @PUT("users/{id}/username")
    Call<Void> updateUserNameById(@Path("id") String userId,
                                  @Body UpdateUsernameRequest request);
    @POST("ai/ask")
    Call<String> askAI(@Body RequestBody body);
    //lay streak user
    @GET("users/{id}/streak")
    Call<StreakResponse> getUserStreak(@Path("id") String userId);

    @PUT("users/{id}/avatar")
    Call<Void> updateAvatarById(@Path("id") String userId,
                                  @Body UpdateAvatarRequest request);

    @GET("Leaderboards/leaderboard/top/week")
    Call<List<RankingItem>> getTopWeekly(
            @Query("period") String period,
            @Query("top") int top
    );

    @GET("Leaderboards/leaderboard/top/month")
    Call<List<RankingItem>> getTopMonthly(
            @Query("period") String period,
            @Query("top") int top
    );
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
    // hoc trong ngya
    @GET("/api/ai-lesson/lesson-today")
    Call<LessonResponse> getTodayLesson();

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

    // dialect
    @GET("dialects")
    Call<List<Dialect>> getDialects();

    @GET("speaking-ai/drills")
    Call<List<SpeakingDialect>> getSpeakingDrills(@Query("dialectId") String dialectId);
    //cham diem accent
    @Multipart
    @POST("speaking-ai/accent-score")
    Call<EvaluateAccentResponse> evaluateAccent(
            @Part MultipartBody.Part audioFile,
            @Part("DialectId") RequestBody dialectId,
            @Part("TargetText") RequestBody targetText
    );
}