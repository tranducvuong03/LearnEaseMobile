package com.example.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.LoginRequest;
import com.example.mobile.model.LoginResponse;
import com.example.mobile.model.userData.UserResponse;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map; // Cần thiết nếu bạn muốn thêm header tùy chỉnh

// Loại bỏ các import SSL liên quan đến Volley HurlStack nếu bạn không dùng nữa
// import javax.net.ssl.HostnameVerifier;
// import javax.net.ssl.HttpsURLConnection;
// import javax.net.ssl.SSLContext;
// import javax.net.ssl.SSLSession;
// import javax.net.ssl.SSLSocketFactory;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.X509TrustManager;
// import java.security.cert.X509Certificate;


public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private LoginAPI apiService;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnGoogleLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = findViewById(R.id.buttonLogin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Khởi tạo ApiService cho các API CÔNG KHAI (không cần token), ví dụ như API login.
        apiService = RetrofitClient.getApiService(this);

        //login bang google
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnGoogle = findViewById(R.id.buttonGoogleLogin);
        textGoogle = findViewById(R.id.textGoogle);
        loadingGoogle = findViewById(R.id.loadingGoogle);

        btnGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(v -> {
            textGoogle.setVisibility(View.GONE);
            loadingGoogle.setVisibility(View.VISIBLE);

            // Không reset UI ở đây
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }, 1000); // thời gian nhỏ để tạo hiệu ứng loading
        });

        checkLoginStatus()
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPref.getString("auth_token", null);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            LoginAPI protectedApi = RetrofitClient.getPublicApiService(); // hoặc getProtectedApiService nếu đã cấu hình interceptor

            protectedApi.getMyProfile("Bearer " + jwtToken).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (response.code() == 401) {
                        sharedPref.edit().remove("auth_token").apply();
                        Toast.makeText(LoginActivity.this, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();

                    if (token != null && !token.isEmpty()) {
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        prefs.edit().putString("auth_token", token).apply();

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", "Login successful. Token saved.");

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Phản hồi token không hợp lệ từ server.", Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "Login successful but token is null or empty.");
                    }
                } else {
                    String errorMsg = "Login failed. Please try again.";
                    if (response.errorBody() != null) {
                        try {
                            String errorBodyString = response.errorBody().string();
                            Log.e("LoginActivity", "Raw Error Body: " + errorBodyString);
                            if (response.code() == 401) {
                                errorMsg = "Invalid username or password!";
                            } else if (response.code() == 400) {
                                try {
                                    JSONObject errorJson = new JSONObject(errorBodyString);
                                    if (errorJson.has("message")) {
                                        errorMsg = errorJson.getString("message");
                                    } else {
                                        errorMsg = "Bad request. Details: " + errorBodyString;
                                    }
                                } catch (JSONException jsonE) {
                                    errorMsg = "Bad request. Details: " + errorBodyString;
                                }
                            } else {
                                errorMsg = "Server error: " + response.code() + " - " + errorBodyString;
                            }
                        } catch (IOException e) {
                            Log.e("LoginActivity", "Error reading error body", e);
                            errorMsg = "Server error: " + response.code();
                        }
                    } else {
                        errorMsg = "Server error: " + response.code() + " " + response.message();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Login failed: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e("LoginActivity", "Network error during login", t);
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendGoogleTokenToBackend(String idToken) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<LoginResponse> call = apiService.loginWithGoogle(request);

        call.enqueue(new Callback<LoginResponse>() {
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

                    Toast.makeText(LoginActivity.this, "Google login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Google login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Google login error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
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
                e.printStackTrace();
                textGoogle.setVisibility(View.VISIBLE);
                loadingGoogle.setVisibility(View.GONE);
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
}
