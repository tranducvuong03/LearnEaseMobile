package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.AccentAdapter;
import com.example.mobile.model.Accent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore); // layout bạn vừa gửi

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAccents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Accent> accents = Arrays.asList(
                new Accent("American English", "United States"),
                new Accent("British English", "United Kingdom"),
                new Accent("Australian English", "Australia"),
                new Accent("Canadian English", "Canada"),
                new Accent("Irish English", "Ireland"),
                new Accent("South African English", "South Africa")
        );
        recyclerView.setAdapter(new AccentAdapter(accents));

        // Setup BottomNavigationView từ layout included
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.menu_lesson) {
                startActivity(new Intent(this, LearningActivity.class));
                return true;
            } else if (id == R.id.menu_rank) {
                startActivity(new Intent(this, RankingActivity.class));
                return true;
            } else if (id == R.id.menu_explore) {
                return true;
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_explore);
    }
}
