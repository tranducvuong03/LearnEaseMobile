package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.LessonAdapter;
import com.example.mobile.api.LessonAPI;
import com.example.mobile.model.Lesson;
import com.example.mobile.utils.RetrofitClient;
import com.example.mobile.utils.UsagePrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicLessonsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LessonAdapter lessonAdapter;
    private List<Lesson> lessonList = new ArrayList<>();
    private String topicId;
private long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_lessons);

        topicId = getIntent().getStringExtra("topic_id");
        if (topicId == null) {
            Toast.makeText(this, "Không tìm thấy Topic", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewLessons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter kèm listener
        lessonAdapter = new LessonAdapter(lessonList, new LessonAdapter.OnLessonClickListener() {
            @Override
            public void onLessonClick(Lesson lesson) {
                Intent intent = new Intent(TopicLessonsActivity.this, LearningActivity.class);
                intent.putExtra("lesson_id", lesson.getLessonId().toString());
                intent.putExtra("topic_id", topicId);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(lessonAdapter);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchLessons();
    }

    private void fetchLessons() {
        LessonAPI api = RetrofitClient.getRetrofit().create(LessonAPI.class);
        api.getLessonsByTopic(UUID.fromString(topicId)).enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessonList.clear();
                    lessonList.addAll(response.body());
                    lessonAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TopicLessonsActivity.this, "Lỗi tải bài học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Toast.makeText(TopicLessonsActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis(); // bắt đầu tính
    }

    @Override
    protected void onPause() {
        super.onPause();
        long sessionTime = System.currentTimeMillis() - startTime;
        UsagePrefs.saveUsageTime(this, sessionTime); // lưu thời gian dùng
    }
}
