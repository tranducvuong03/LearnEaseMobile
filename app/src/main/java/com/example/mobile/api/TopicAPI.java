package com.example.mobile.api;

import com.example.mobile.model.Topic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TopicAPI {
    @GET("topics/with-progress/{userId}")
    Call<List<Topic>> getTopicsWithProgress(@Path("userId") String userId);
}
