package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.ChatAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.Message;
import com.example.mobile.utils.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText editMessage;
    private ImageButton btnSend;

    private LoginAPI apiService;

    private static final String PREF_NAME = "chat_prefs";
    private static final String CHAT_HISTORY_KEY = "chat_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        recyclerView = findViewById(R.id.chatRecyclerView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        ImageView backButton = findViewById(R.id.backButton);

        apiService = RetrofitClient.getApiService(this);

        adapter = new ChatAdapter(loadMessagesFromStorage());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    Message userMessage = new Message(messageText, true);
                    adapter.addMessage(userMessage);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    editMessage.setText("");
                    saveMessagesToStorage(adapter.getMessages());

                    askAiApi(messageText);
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Đóng ChatActivity để không quay lại khi nhấn nút back hệ thống
            }
        });

    }

    private void askAiApi(String message) {
        RequestBody body = RequestBody.create(
                "\"" + message + "\"",  // phải thêm dấu ngoặc kép xung quanh
                MediaType.parse("application/json; charset=utf-8")
        );

        apiService.askAI(body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String aiText = response.body();
                    Message aiMessage = new Message(aiText, false);

                    runOnUiThread(() -> {
                        adapter.addMessage(aiMessage);
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        saveMessagesToStorage(adapter.getMessages());
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Message errorMessage = new Message("Lỗi phản hồi từ AI", false);
                adapter.addMessage(errorMessage);
                saveMessagesToStorage(adapter.getMessages());
            }
        });
    }


    private void saveMessagesToStorage(List<Message> messages) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(messages);
        editor.putString(CHAT_HISTORY_KEY, json);
        editor.apply();
    }

    private List<Message> loadMessagesFromStorage() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = prefs.getString(CHAT_HISTORY_KEY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Message>>(){}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }
}
