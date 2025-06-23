package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobile.api.ApiService;
import com.example.mobile.model.userData.UpdateUsernameRequest;
import com.example.mobile.model.userData.UserResponse;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.LoadingManager;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView tvUserName, tvNewbieTag;
    private ImageView ivProfilePic;
    private ApiService apiService;
    private String userId;
    private void showEditUsernameDialog(String currentUsername) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_username, null);
        builder.setView(dialogView);

        EditText etUsername = dialogView.findViewById(R.id.et_username_input);
        etUsername.setText(currentUsername);

        builder.setPositiveButton("Update", null); // Gắn sau để kiểm tra không rỗng
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button updateBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            updateBtn.setOnClickListener(v -> {
                String newUsername = etUsername.getText().toString().trim();
                if (!newUsername.isEmpty()) {
                    updateUserNameAPI(userId, newUsername);
                    dialog.dismiss();
                } else {
                    etUsername.setError("Username cannot be empty");
                }
            });
        });

        dialog.show();
    }


    private void updateUserNameAPI(String userId, String newName) {
        UpdateUsernameRequest request = new UpdateUsernameRequest(newName);
        apiService.updateUserNameById(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Tên đã được cập nhật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvUserName = findViewById(R.id.tv_user_name);
        tvNewbieTag = findViewById(R.id.tv_newbie_tag);
        ivProfilePic = findViewById(R.id.iv_profile_pic);

        apiService = RetrofitClient.getApiService(this);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        if (token != null) {
            ApiCaller.callWithLoading(this,
                    apiService.getMyProfile("Bearer " + token),
                    new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            LoadingManager.getInstance().dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UserResponse user = response.body();
                                userId = user.getUserId();
                                tvUserName.setText(user.getUsername());
                                tvNewbieTag.setText(user.getEmail());

                                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                                    Glide.with(ProfileActivity.this)
                                            .load(user.getAvatarUrl())
                                            .into(ivProfilePic);
                                }
                            } else {
                                Log.e(TAG, "Failed with code: " + response.code());
                                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            LoadingManager.getInstance().dismiss();
                            Log.e(TAG, "Error: " + t.getMessage());
                            Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
            return;
        }
        // --- Edit username ---
        ImageButton editProfileButton = findViewById(R.id.btn_edit_profile);
        TextView userNameTextView = findViewById(R.id.tv_user_name);

        editProfileButton.setOnClickListener(v -> {
            showEditUsernameDialog(userNameTextView.getText().toString());
        });

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
            logoutAccountTv.setOnClickListener(v -> {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            // 1. Xóa token
                            prefs.edit().clear().apply();

                            // 2. Đăng xuất Google nếu dùng
                            GoogleSignIn.getClient(ProfileActivity.this,
                                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                            ).signOut();

                            // 3. Chuyển về màn hình Login và xoá back stack
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish(); // đảm bảo thoát ProfileActivity
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });


        }

        // --- Buttons ---
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

//        ImageButton btnEditProfilee = findViewById(R.id.btn_edit_profile);
//        if (btnEditProfilee != null) {
//            btnEditProfilee.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Edit Profile clicked (Functionality not implemented)", Toast.LENGTH_SHORT).show());
//        }

    }


}