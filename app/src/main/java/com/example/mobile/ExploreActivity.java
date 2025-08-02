package com.example.mobile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.AccentAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.dialog.SampleDialogFragment;
import com.example.mobile.model.Dialect;
import com.example.mobile.model.SpeakingDialect;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccentAdapter adapter;
    private LoginAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        api = RetrofitClient.getApiService(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerViewAccents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccentAdapter(new ArrayList<>(), this::onAccentClicked);
        recyclerView.setAdapter(adapter);

        loadAccentList();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(ExploreActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.menu_lesson) {
                    startActivity(new Intent(ExploreActivity.this, TopicActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.menu_challenge) {
                    startActivity(new Intent(ExploreActivity.this, RankingActivity.class));
                    return true;
                } else if (id == R.id.menu_explore) {
                    return true;
                } else if (id == R.id.menu_profile) {
                    startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_explore);
    }

    private void loadAccentList() {
        ApiCaller.callWithLoading(this, api.getDialects(), new Callback<List<Dialect>>() {
            @Override
            public void onResponse(Call<List<Dialect>> call, Response<List<Dialect>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Dialect> all = response.body();
                    List<Dialect> top5 = all.subList(0, Math.min(5, all.size())); // ✅ lấy 5 dialect đầu tiên
                    adapter.setData(top5);
                } else {
                    Toast.makeText(ExploreActivity.this, "❌ Không tải được danh sách accent", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Dialect>> call, Throwable t) {
                Toast.makeText(ExploreActivity.this, "⚠️ Lỗi mạng hoặc server", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void onAccentClicked(Dialect dialect) {
        ApiCaller.callWithLoading(this, api.getSpeakingDrills(dialect.getDialectId()), new Callback<List<SpeakingDialect>>() {
            @Override
            public void onResponse(Call<List<SpeakingDialect>> call, Response<List<SpeakingDialect>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SpeakingDialect> drills = response.body();
                    if (!drills.isEmpty()) {
                        Intent intent = new Intent(ExploreActivity.this, AccentPracticeActivity.class);
                        intent.putExtra("dialectId", dialect.getDialectId().toString());
                        intent.putExtra("region", dialect.getRegion());
                        intent.putExtra("drills", new ArrayList<>(drills)); // phải implements Serializable trong SpeakingDialect
                        startActivity(intent);
                    } else {
                        Toast.makeText(ExploreActivity.this, "Không có dữ liệu luyện nói", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExploreActivity.this, "❌ Không có dữ liệu luyện nói", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SpeakingDialect>> call, Throwable t) {
                Toast.makeText(ExploreActivity.this, "⚠️ Lỗi tải voice sample", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showVoiceDialog(String dialectName, List<SpeakingDialect> drills) {
        StringBuilder builder = new StringBuilder();

        boolean hasAudio = false;

        for (SpeakingDialect drill : drills) {
            builder.append("• ").append(drill.getPrompt()).append("\n");
            if (drill.getAudioUrl() != null && !drill.getAudioUrl().isEmpty()) {
                builder.append("🔊 ").append(drill.getAudioUrl()).append("\n\n");
                hasAudio = true;
            } else {
                builder.append("(Chưa có audio)\n\n");
            }
        }

        if (!hasAudio) {
            builder.insert(0, "⚠️ Accent này chưa có voice sample.\n\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Samples - " + dialectName)
                .setMessage(builder.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

}
