package com.example.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.ChatAdapter;
import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.Message;
import com.example.mobile.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
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

    private void askAiApi(String message) {
        // Gọi API
        RequestBody body = RequestBody.create(
                message, MediaType.parse("text/plain")
        );
        Map<String, String> data = new HashMap<>();
        data.put("userInput", message);
        apiService.askAI(data).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String aiText = response.body();
                    Message aiMessage = new Message(aiText, false);

                    // Chuyển về UI thread
                    runOnUiThread(() -> {
                        adapter.addMessage(aiMessage);
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    });
                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Message errorMessage = new Message("Lỗi phản hồi từ AI", false);
                adapter.addMessage(errorMessage);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        recyclerView = findViewById(R.id.chatRecyclerView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        apiService = RetrofitClient.getApiService(this);
        adapter = new ChatAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editMessage.getText().toString().trim();
                askAiApi(messageText);
                if (!messageText.isEmpty()) {
                    Message userMessage = new Message(messageText, true);
                    adapter.addMessage(userMessage);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    editMessage.setText("");

                }
            }
        });

    }
}
