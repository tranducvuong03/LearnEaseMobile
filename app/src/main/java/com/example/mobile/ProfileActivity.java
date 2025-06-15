package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // --- Bottom Navigation View Setup ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.menu_home) {
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (id == R.id.menu_lesson) { // Đã sửa từ menu_practice sang menu_lesson để khớp với bottom_nav_menu.xml
                        startActivity(new Intent(ProfileActivity.this, LearningActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (id == R.id.menu_practice) { // Giữ nguyên nếu bạn có ý định dùng nó
                        Toast.makeText(ProfileActivity.this, "Practice clicked (functionality not implemented)", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.menu_explore) {
                        startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (id == R.id.menu_profile) {
                        return true;
                    }
                    return false;
                }
            });
            bottomNavigationView.setSelectedItemId(R.id.menu_profile);
        } else {
            Log.e(TAG, "BottomNavigationView (ID: R.id.bottom_navigation) not found in activity_profile.xml. Double check layout structure and included file!");
            Toast.makeText(this, "Error: Navigation bar not found! Check logs.", Toast.LENGTH_LONG).show();
        }

        // --- Other Navigation Items Setup (Dashboard Section) ---
        View settingsItem = findViewById(R.id.item_settings);
        if (settingsItem != null) {
            settingsItem.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));
        }

        View achievementsItem = findViewById(R.id.item_achievements);
        if (achievementsItem != null) {
            achievementsItem.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, AchievementsActivity.class)));
        }

        View subscriptionItem = findViewById(R.id.item_subscription);
        if (subscriptionItem != null) {
            subscriptionItem.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SubscriptionActivity.class)));
        }

        View activitiesItem = findViewById(R.id.item_activities);
        if (activitiesItem != null) {
            activitiesItem.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ActivitiesActivity.class)));
        }

        // --- My Account Section Items ---
        View switchAccountTv = findViewById(R.id.tv_switch_account);
        if (switchAccountTv != null) {
            switchAccountTv.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Switch Account clicked (Functionality not implemented)", Toast.LENGTH_SHORT).show());
        }

        View logoutAccountTv = findViewById(R.id.tv_logout_account);
        if (logoutAccountTv != null) {
            logoutAccountTv.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Logout clicked (Functionality not implemented)", Toast.LENGTH_SHORT).show());
        }

        // --- Buttons ---
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        ImageButton btnEditProfile = findViewById(R.id.btn_edit_profile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Edit Profile clicked (Functionality not implemented)", Toast.LENGTH_SHORT).show());
        }
    }
}