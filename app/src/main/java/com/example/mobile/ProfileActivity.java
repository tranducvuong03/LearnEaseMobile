package com.example.mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.menu_practice) {
                    startActivity(new Intent(ProfileActivity.this, LearningActivity.class));
                    return true;
                } else if (id == R.id.menu_explore) {
                    startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                    return true;
                } else if (id == R.id.menu_profile) {
                    // Already on Profile
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
    }
} 