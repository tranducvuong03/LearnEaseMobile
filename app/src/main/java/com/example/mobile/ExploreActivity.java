package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import java.util.Arrays;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Toolbar back
        MaterialToolbar toolbar = findViewById(R.id.menu_explore);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // RecyclerView accents
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

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(ExploreActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.menu_practice) {
                    startActivity(new Intent(ExploreActivity.this, LearningActivity.class));
                    return true;
                } else if (id == R.id.menu_explore) {
                    // Already on Explore
                    return true;
                } else if (id == R.id.menu_profile) {
                    startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_explore);
    }
}

class Accent {
    String name;
    String country;
    Accent(String name, String country) {
        this.name = name;
        this.country = country;
    }
} 