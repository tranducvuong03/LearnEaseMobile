package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.userData.UpdateAvatarRequest;
import com.example.mobile.model.userData.UpdateUsernameRequest;
import com.example.mobile.model.userData.UserResponse;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.CloudinaryManager;
import com.example.mobile.utils.LoadingManager;
import com.example.mobile.utils.RetrofitClient;
import com.example.mobile.utils.UsagePrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView tvUserName, tvNewbieTag;
    private ImageView ivProfilePic;
    private LoginAPI apiService;
    private String userId;
    private static final int PICK_IMAGE = 1;

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


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);

    }
    private void uploadAvatarToCloudinary(Uri imageUri) {
        MediaManager.get().upload(imageUri)
                .unsigned("LearnEase") // 👈 thay bằng preset name thật
                .option("resource_type", "image")
                .constrain(TimeWindow.immediate()) // 👈 ép upload ngay, không dùng JobScheduler
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = resultData.get("secure_url").toString();
                        runOnUiThread(() -> {
                            Glide.with(ProfileActivity.this).load(imageUrl).into(ivProfilePic);
                            updateAvatarAPI(userId, imageUrl);
                        });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        runOnUiThread(() ->
                                Toast.makeText(ProfileActivity.this, "Upload lỗi: " + error.getDescription(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }


    private void updateAvatarAPI(String userId, String imageUrl) {
        UpdateAvatarRequest request = new UpdateAvatarRequest(imageUrl); // nếu chỉ cần avatarUrl
        apiService.updateAvatarById(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Ảnh đại diện đã được cập nhật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi cập nhật ảnh: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                uploadAvatarToCloudinary(selectedImage);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cloudinary chỉ nên init 1 lần
        CloudinaryManager.init(this);
        setContentView(R.layout.activity_profile);
        tvUserName = findViewById(R.id.tv_user_name);
        tvNewbieTag = findViewById(R.id.tv_newbie_tag);
        ivProfilePic = findViewById(R.id.iv_profile_pic);

        apiService = RetrofitClient.getApiService(this);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences prefsStreak = getSharedPreferences("SkillDonePrefs", MODE_PRIVATE);
        SharedPreferences prefsChatMessages = getSharedPreferences("chat_prefs", MODE_PRIVATE);
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

        // --- Edit avayar ---

        ivProfilePic.setOnClickListener(v -> selectImage());

        // Bắt sự kiện click ảnh
        ivProfilePic.setOnClickListener(v -> selectImage());

        // get thời gian học
        long totalMillis = UsagePrefs.getAllTimeUsage(this);
        long totalMinutes = totalMillis / 1000 / 60;
        TextView tvTotalLearn = findViewById(R.id.tv_total_learn);
        tvTotalLearn.setText(totalMinutes + " mins");

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
                        finish();
                        return true;
                    } else if (id == R.id.menu_lesson) {
                        startActivity(new Intent(ProfileActivity.this, TopicActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    } else if (id == R.id.menu_challenge) {
                        startActivity(new Intent(ProfileActivity.this, RankingActivity.class));
                        return true;
                    } else if (id == R.id.menu_explore) {
                        startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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
        View settingsItem = findViewById(R.id.item_shopping);
        if (settingsItem != null) {
            settingsItem.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ShopActivity.class)));
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
            switchAccountTv.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Sorry, your version does not support this feature.", Toast.LENGTH_SHORT).show());
        }

        View logoutAccountTv = findViewById(R.id.tv_logout_account);
        if (logoutAccountTv != null) {
            logoutAccountTv.setOnClickListener(v -> {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Log Out", (dialog, which) -> {
                            // 1. Xóa token
                            prefs.edit().remove("auth_token").apply();
                            prefs.edit().remove("user_id").apply();
                            prefsChatMessages.edit().remove("chat_history").apply();
                            prefsStreak.edit().remove("streak").apply();

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
                        .setNegativeButton("Cancel", null)
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