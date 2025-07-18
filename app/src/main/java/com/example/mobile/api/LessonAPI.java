package com.example.mobile.api;

import com.example.mobile.model.Lesson;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LessonAPI {
    @GET("lesson/by-topic/{topicId}")
    Call<List<Lesson>> getLessonsByTopic(@Path("topicId") UUID topicId);
}
