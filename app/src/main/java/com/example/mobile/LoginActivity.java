package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.jwt.JWT;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.LoginRequest;
import com.example.mobile.model.LoginResponse;
import com.example.mobile.model.userData.UserResponse;
import com.example.mobile.model.GoogleLoginRequest;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextUsername, editTextPassword;
    private View btnGoogle;
    private ProgressBar loadingGoogle;
    private LoginAPI apiService;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private View textGoogle;
    private View buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        btnGoogle = findViewById(R.id.buttonGoogleLogin);
        textGoogle = findViewById(R.id.textGoogle);
        loadingGoogle = findViewById(R.id.loadingGoogle);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, StarterActivity.class);
                startActivity(intent);
                finish(); // Đóng ChatActivity để không quay lại khi nhấn nút back hệ thống
            }
        });

        apiService = RetrofitClient.getApiService(this);

        buttonLogin.setOnClickListener(v -> login());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(v -> {
            textGoogle.setVisibility(View.GONE);
            loadingGoogle.setVisibility(View.VISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                });
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }, 1000);
        });

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPref.getString("auth_token", null);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            apiService.getMyProfile("Bearer " + jwtToken).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else if (response.code() == 401) {
                        sharedPref.edit().remove("auth_token").apply();
                        Toast.makeText(LoginActivity.this, "Phiên đăng nhập đã hết hạn.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Log.e("LoginActivity", "Error checking token", t);
                    Toast.makeText(LoginActivity.this, "Không kiểm tra được trạng thái đăng nhập.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void login() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    if (token != null && !token.isEmpty()) {
                        try {
                            JWT jwt = new JWT(token);

                            String userId = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString();
                            String username = jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name").asString();

                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("auth_token", token)
                                    .putString("user_id", userId)
                                    .putString("user_name", username)
                                    .apply();

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Token rỗng.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleLoginError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginError(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            JSONObject json = new JSONObject(errorBody);
            String message = json.optString("message", "Đăng nhập thất bại.");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            Toast.makeText(this, "Đăng nhập thất bại: lỗi không xác định", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendGoogleTokenToBackend(String idToken) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        apiService.loginWithGoogle(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                textGoogle.setVisibility(View.VISIBLE);
                loadingGoogle.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    UserResponse userResponse = loginResponse.getUser();
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("auth_token", token)
                            .putString("user_id", userResponse.getUserId())
                            .putString("user_name", userResponse.getUsername())
                            .putString("user_email", userResponse.getEmail())
                            .putString("user_avatar", userResponse.getAvatarUrl())
                            .apply();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Google login thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                textGoogle.setVisibility(View.VISIBLE);
                loadingGoogle.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Lỗi Google login: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    sendGoogleTokenToBackend(account.getIdToken());
                }
            } catch (ApiException e) {
                textGoogle.setVisibility(View.VISIBLE);
                loadingGoogle.setVisibility(View.GONE);
                Toast.makeText(this, "Google Sign-In thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
