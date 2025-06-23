package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// Loại bỏ các import Volley không cần thiết nếu bạn không dùng Volley nữa
// import com.android.volley.Request;
// import com.android.volley.RequestQueue;
// import com.android.volley.toolbox.JsonObjectRequest;
// import com.android.volley.toolbox.Volley;
// import com.android.volley.toolbox.HurlStack;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.model.userData.UserResponse;
import com.example.mobile.utils.ApiCaller;
import com.example.mobile.utils.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

// Import các lớp Retrofit và API mới của bạn
import com.example.mobile.api.ApiService;
import com.example.mobile.model.LoginRequest; // Bạn cần tạo lớp này cho body request
import com.example.mobile.model.LoginResponse; // Bạn cần tạo lớp này cho response

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

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
    // Không cần RequestQueue của Volley nữa
    // private RequestQueue requestQueue;

    // Không cần API_LOGIN_URL riêng nữa vì nó sẽ nằm trong ApiService.BASE_URL
    // private final String API_LOGIN_URL = "https://10.0.2.2:7083/api/auth/login";

    // Khai báo ApiService
    private ApiService apiService;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnGoogleLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Khởi tạo ApiService thông qua RetrofitClient.
        // RetrofitClient sẽ tự động xử lý SSL bypass và các Interceptor khác (như logging).
        // KHÔNG CẦN DÙNG getSslSocketFactory() ở đây nữa vì RetrofitClient đã lo.
        apiService = RetrofitClient.getApiService(this);

        //login bang google
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnGoogleLogin = findViewById(R.id.buttonGoogleLogin); // Nút trong XML

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id)) // client_id lấy từ Google Cloud
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleLogin.setOnClickListener(v -> {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        });


    }

    //login username&password
    private void login() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng LoginRequest thay vì JSONObject
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Sử dụng ApiService để gọi API đăng nhập
        // Bạn cần thêm phương thức login vào ApiService
        Call<LoginResponse> call = apiService.login(loginRequest);

        ApiCaller.callWithLoading(this, call, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();

                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    prefs.edit().putString("auth_token", token).apply();

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Login failed. Please try again.";
                    if (response.errorBody() != null) {
                        try {
                            // Cố gắng đọc lỗi từ errorBody nếu server trả về JSON lỗi
                            // Bạn có thể cần một Gson để parse errorBody thành một ErrorResponse Model
                            // Ví dụ đơn giản:
                            String errorBodyString = response.errorBody().string();
                            if (response.code() == 401) {
                                errorMsg = "Invalid username or password!";
                            } else if (response.code() == 400) {
                                errorMsg = "Bad request: " + errorBodyString;
                            } else {
                                errorMsg = "Server error: " + response.code() + " - " + errorBodyString;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            errorMsg = "Server error: " + response.code();
                        }
                    } else {
                        errorMsg = "Server error: " + response.code() + " " + response.message();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    //login by google
    private void sendGoogleTokenToBackend(String idToken) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<LoginResponse> call = apiService.loginWithGoogle(request); // Phải khai báo trong ApiService

        ApiCaller.callWithLoading(this, call, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
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
                    sendGoogleTokenToBackend(account.getIdToken()); // gọi hàm gửi token
                }
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // KHÔNG CẦN PHƯƠNG THỨC getSslSocketFactory() NỮA VÌ NÓ ĐÃ ĐƯỢC CHUYỂN VÀO RetrofitClient
    // private SSLSocketFactory getSslSocketFactory() { /* ... */ }
}