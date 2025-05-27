package com.example.mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;

public class LearningActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(LearningActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.menu_practice) {
                    // Already on Practice
                    return true;
                } else if (id == R.id.menu_explore) {
                    startActivity(new Intent(LearningActivity.this, ExploreActivity.class));
                    return true;
                } else if (id == R.id.menu_profile) {
                    startActivity(new Intent(LearningActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_practice);
    }
} 