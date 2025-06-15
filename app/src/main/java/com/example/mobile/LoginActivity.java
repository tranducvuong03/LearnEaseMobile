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

import com.example.mobile.utils.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

// Import các lớp Retrofit và API mới của bạn
import com.example.mobile.api.ApiService;
import com.example.mobile.model.LoginRequest; // Bạn cần tạo lớp này cho body request
import com.example.mobile.model.LoginResponse; // Bạn cần tạo lớp này cho response

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


public class LoginActivity extends Activity {
    private TextInputEditText editTextUsername, editTextPassword;
    // Không cần RequestQueue của Volley nữa
    // private RequestQueue requestQueue;

    // Không cần API_LOGIN_URL riêng nữa vì nó sẽ nằm trong ApiService.BASE_URL
    // private final String API_LOGIN_URL = "https://10.0.2.2:7083/api/auth/login";

    // Khai báo ApiService
    private ApiService apiService;

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

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

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

        call.enqueue(new Callback<LoginResponse>() {
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

    // KHÔNG CẦN PHƯƠNG THỨC getSslSocketFactory() NỮA VÌ NÓ ĐÃ ĐƯỢC CHUYỂN VÀO RetrofitClient
    // private SSLSocketFactory getSslSocketFactory() { /* ... */ }
}