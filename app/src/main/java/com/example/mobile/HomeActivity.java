package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    // Already on Home
                    return true;
                } else if (id == R.id.menu_practice) {
                    startActivity(new Intent(HomeActivity.this, LearningActivity.class));
                    return true;
                } else if (id == R.id.menu_explore) {
                    startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                    return true;
                } else if (id == R.id.menu_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
    }
} 