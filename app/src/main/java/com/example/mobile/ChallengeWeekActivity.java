package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.ChallengeWeekAdapter;
import com.example.mobile.model.ChallengeStatus;
import com.example.mobile.model.ChallengeWeek;

import java.util.Arrays;
import java.util.List;

public class ChallengeWeekActivity extends AppCompatActivity {
    private RecyclerView recyclerWeek;
    private ChallengeWeekAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_week); // layout XML bên dưới
        ImageView backButton = findViewById(R.id.backButton);
        recyclerWeek = findViewById(R.id.recyclerWeek);
        recyclerWeek.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChallengeWeekActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Đóng ChatActivity để không quay lại khi nhấn nút back hệ thống
            }
        });
        List<ChallengeWeek> weekList = Arrays.asList(
                new ChallengeWeek("Mo", ChallengeStatus.CHECKED),
                new ChallengeWeek("Tu", ChallengeStatus.MISSED),
                new ChallengeWeek("We", ChallengeStatus.MISSED),
                new ChallengeWeek("Th", ChallengeStatus.CHECKED),
                new ChallengeWeek("Fr", ChallengeStatus.MISSED),
                new ChallengeWeek("Sa", ChallengeStatus.UNCHECKED),
                new ChallengeWeek("Su", ChallengeStatus.UNCHECKED)
        );

        adapter = new ChallengeWeekAdapter(weekList);
        recyclerWeek.setAdapter(adapter);
    }
}
