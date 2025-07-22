package com.example.mobile;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.EvaluateAccentResponse;
import com.example.mobile.model.SpeakingDialect;
import com.example.mobile.utils.ApiCaller;

import com.example.mobile.utils.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccentPracticeActivity extends AppCompatActivity {
    private TextView textPrompt, textScore, textFeedback;
    private Button btnPlay, btnRecord, btnNext;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String audioPath;

    private List<SpeakingDialect> drills;
    private int currentIndex = 0;
    private LoginAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        api = RetrofitClient.getApiService(this);

        textPrompt = findViewById(R.id.textPrompt);
        textScore = findViewById(R.id.textScore);
        textFeedback = findViewById(R.id.textFeedback);
        btnPlay = findViewById(R.id.btnPlaySample);
        btnRecord = findViewById(R.id.btnRecord);
        btnNext = findViewById(R.id.btnNext);

        Intent intent = getIntent();
        drills = (List<SpeakingDialect>) getIntent().getSerializableExtra("drills");
        String dialectId = intent.getStringExtra("dialectId");

        if (drills == null || drills.isEmpty()) {
            Toast.makeText(this, "Không có câu luyện nói.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnPlay.setOnClickListener(v -> playSample());
        btnRecord.setOnClickListener(v -> {
            if (mediaRecorder == null) startRecording();
            else stopRecordingAndEvaluate(dialectId);
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < drills.size() - 1) {
                currentIndex++;
                showCurrentDrill();
            } else {
                Toast.makeText(this, "✅ Bạn đã hoàn thành tất cả câu luyện!", Toast.LENGTH_SHORT).show();
            }
        });

        showCurrentDrill();
    }

    private void showCurrentDrill() {
        SpeakingDialect drill = drills.get(currentIndex);
        textPrompt.setText(drill.getPrompt());
        textScore.setText("Score: --");
        textFeedback.setText("Feedback sẽ hiển thị ở đây...");
    }

    private void playSample() {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(drills.get(currentIndex).getAudioUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecording() {
        audioPath = getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/recorded.mp3";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(audioPath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            btnRecord.setText("✊ Stop & Evaluate");
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi ghi âm", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecordingAndEvaluate(String dialectId) {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            btnRecord.setText("🎤 Ghi âm lại");
            sendForEvaluation(dialectId);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi dừng ghi âm", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendForEvaluation(String dialectId) {
        File file = new File(audioPath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("audio/mp3"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("AudioFile", file.getName(), reqFile);
        RequestBody dialectPart = RequestBody.create(MediaType.parse("text/plain"), dialectId);

        ApiCaller.callWithLoading(this, api.evaluateAccent(body, dialectPart), new Callback<EvaluateAccentResponse>() {
            @Override
            public void onResponse(Call<EvaluateAccentResponse> call, Response<EvaluateAccentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EvaluateAccentResponse result = response.body();
                    textScore.setText("Score: " + result.getScore());
                    textFeedback.setText(result.getFeedback());
                } else {
                    Toast.makeText(AccentPracticeActivity.this, "❌ Không chấm được điểm.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EvaluateAccentResponse> call, Throwable t) {
                Toast.makeText(AccentPracticeActivity.this, "⚠️ Lỗi gửi yêu cầu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
