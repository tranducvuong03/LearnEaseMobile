package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private RequestQueue requestQueue;

    // Dùng 10.0.2.2 để Android Emulator gọi vào localhost máy host
    private final String API_LOGIN_URL = "http://10.0.2.2:7083/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        requestQueue = Volley.newRequestQueue(this);

        buttonLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", email); // Đổi sang "email" nếu backend yêu cầu
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_LOGIN_URL,
                loginData,
                response -> {
                    try {
                        String token = response.getString("token");

                        // Lưu token vào SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        prefs.edit().putString("auth_token", token).apply();

                        Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();

                        // Chuyển sang HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Không cho người dùng quay lại màn hình Login
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    String errorMsg = "Login failed";
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        errorMsg = "Invalid credentials!";
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
