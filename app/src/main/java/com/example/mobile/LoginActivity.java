package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View; // Import View
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;

import com.android.volley.toolbox.HurlStack;

public class LoginActivity extends Activity {
    private TextInputEditText editTextUsername, editTextPassword;
    private RequestQueue requestQueue;

    // Đã chuyển sang HTTPS vì lỗi trước đó cho thấy bạn đang dùng HTTPS hoặc cần dùng HTTPS
    // Đảm bảo backend của bạn cũng đang chạy trên HTTPS và cùng port
    private final String API_LOGIN_URL = "https://10.0.2.2:7083/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Khởi tạo RequestQueue với cấu hình SSL tùy chỉnh
        requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSslSocketFactory()));

        // Thay đổi từ lambda expression sang cú pháp ghi đè onClick truyền thống
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

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating login request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_LOGIN_URL,
                loginData,
                response -> {
                    try {
                        String token = response.getString("token");
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        prefs.edit().putString("auth_token", token).apply();

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Login successful but no token received", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String errorMsg = "Login failed. Please try again.";
                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 401:
                                errorMsg = "Invalid username or password!";
                                break;
                            case 400:
                                errorMsg = "Bad request. Please check your input.";
                                break;
                            default:
                                errorMsg = "Server error: " + error.networkResponse.statusCode;
                                break;
                        }
                    } else if (error.getMessage() != null) {
                        errorMsg = "Network error: " + error.getMessage();
                        error.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Bỏ qua việc kiểm tra chứng chỉ SSL. KHÔNG BAO GIỜ SỬ DỤNG TRONG MÔI TRƯỜNG PRODUCTION!
     * Phương pháp này tạo ra một SSLSocketFactory tin cậy tất cả các chứng chỉ.
     *
     * @return SSLSocketFactory đã được cấu hình để tin cậy tất cả các chứng chỉ.
     */
    private SSLSocketFactory getSslSocketFactory() {
        try {
            // Tạo TrustManager tin cậy tất cả các chứng chỉ
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Khởi tạo SSLContext với TrustManager đã tạo
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Thiết lập HostnameVerifier để chấp nhận tất cả các hostnames
            // Điều này giải quyết các lỗi liên quan đến tên host trong chứng chỉ
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true; // Luôn trả về true để chấp nhận bất kỳ hostname nào
                }
            });

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi, nhưng điều này không mong muốn
        }
    }
}