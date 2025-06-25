package com.example.mobile.api;

import com.example.mobile.GoogleLoginRequest;
import com.example.mobile.model.CheckoutResponse;
import com.example.mobile.model.LoginRequest;
import com.example.mobile.model.LoginResponse;
import com.example.mobile.model.NextLessonModel;
import com.example.mobile.model.SubscriptionInfo;
import com.example.mobile.model.VocabularyItem;
import com.example.mobile.model.SpeakingExercise;
import com.example.mobile.model.userData.UpdateAvatarRequest;
import com.example.mobile.model.userData.UpdateUsernameRequest;
import com.example.mobile.model.userData.UserResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    String BASE_URL = "https://10.0.2.2:7083/api/";

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/google-login")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);
    @GET("users/me")
    Call<UserResponse> getMyProfile(@Header("Authorization") String token);

    @PUT("users/{id}/username")
    Call<Void> updateUserNameById(@Path("id") String userId,
                                  @Body UpdateUsernameRequest request);

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

    @GET("Learning/next-lesson")
    Call<NextLessonModel> getNextLessonForUser();

    //----------------subscription------------------------//
    @POST("/api/subscription/pay")
    Call<CheckoutResponse> createSubscription(@Body Map<String, String> planType);
    @GET("/api/subscription/me")
    Call<SubscriptionInfo> getMySubscription();

}