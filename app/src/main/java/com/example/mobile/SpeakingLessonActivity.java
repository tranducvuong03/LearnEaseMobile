package com.example.mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mobile.api.CompareSpeakingAPI;
import com.example.mobile.utils.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeakingLessonActivity extends AppCompatActivity {

    private static final int REQ_AUDIO_PERM = 200;

    private TextView tvPhrase, tvHold, tvScore;
    private ImageButton btnMic;
    private Button btnNext;
    private LinearLayout layoutAudio;
    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private File userAudioFile;
    private String sampleAudioUrl;
    private String prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_test);

        tvPhrase    = findViewById(R.id.tvPhrase);
        tvHold      = findViewById(R.id.tvHold);
        tvScore     = findViewById(R.id.tvScore);
        btnMic      = findViewById(R.id.btnMic);
        btnNext     = findViewById(R.id.btnNext);
        layoutAudio = findViewById(R.id.layoutAudio);
        mediaPlayer = new MediaPlayer();

        prompt         = getIntent().getStringExtra("PROMPT");
        sampleAudioUrl = getIntent().getStringExtra("SAMPLE_AUDIO_URL");
        if (prompt != null) tvPhrase.setText("'" + prompt + "'");

        layoutAudio.setOnClickListener(v -> playSampleAudio());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO_PERM);
        }

        btnMic.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRecordingAndSend();
                    return true;
            }
            return false;
        });

        btnNext.setOnClickListener(v -> finish());
    }

    private void startRecording() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (dir != null && !dir.exists()) dir.mkdirs();
            userAudioFile = new File(dir, "user_" + System.currentTimeMillis() + ".wav");

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // WAV không hỗ trợ tốt → dùng MPEG_4
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioSamplingRate(16000);
            recorder.setOutputFile(userAudioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            tvHold.setText("Recording…");
        } catch (IOException ex) {
            Toast.makeText(this, "Không thể ghi âm: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Recorder", "startRecording", ex);
        }
    }

    private void stopRecordingAndSend() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            tvHold.setText("Đang chấm điểm…");
            uploadAndCompare(userAudioFile, sampleAudioUrl);
        } catch (Exception ex) {
            Log.e("Recorder", "stopRecording", ex);
        }
    }

    private void uploadAndCompare(File userFile, String sampleUrl) {
        CompareSpeakingAPI api = RetrofitClient.getCompareApiService(this);

        RequestBody audioBody = RequestBody.create(userFile, MediaType.parse("audio/mp4")); // tương ứng với MPEG_4
        MultipartBody.Part userPart = MultipartBody.Part.createFormData("user_audio",
                userFile.getName(), audioBody);
        RequestBody urlBody = RequestBody.create(sampleUrl, MediaType.parse("text/plain"));

        api.compareWithUrl(userPart, urlBody).enqueue(new Callback<CompareSpeakingAPI.SimilarityRes>() {
            @Override
            public void onResponse(Call<CompareSpeakingAPI.SimilarityRes> call,
                                   Response<CompareSpeakingAPI.SimilarityRes> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    showToast("API lỗi: " + resp.code());
                    tvHold.setText("Hold To Pronounce");
                    return;
                }
                float sim = resp.body().similarity;
                showResult(sim);
            }

            @Override
            public void onFailure(Call<CompareSpeakingAPI.SimilarityRes> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
                tvHold.setText("Hold To Pronounce");
            }
        });
    }

    private void showResult(float sim) {
        tvScore.setVisibility(View.VISIBLE);
        tvScore.setText(String.format(Locale.US, "Similarity: %.0f%%", sim * 100));

        if (sim >= 0.85f) {
            tvHold.setText("Great! Pronunciation matched.");
            btnNext.setVisibility(View.VISIBLE);
        } else {
            tvHold.setText("Low score, try again.");
            btnNext.setVisibility(View.GONE);
        }
    }

    private void playSampleAudio() {
        if (sampleAudioUrl == null || sampleAudioUrl.isEmpty()) {
            showToast("Không có audio mẫu");
            return;
        }
        try {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(sampleAudioUrl));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            showToast("Không thể phát audio: " + ex.getMessage());
        }
    }

    private void showToast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int rq, @NonNull String[] p, @NonNull int[] g) {
        super.onRequestPermissionsResult(rq, p, g);
        if (rq == REQ_AUDIO_PERM && (g.length == 0 || g[0] != PackageManager.PERMISSION_GRANTED)) {
            showToast("Từ chối quyền ghi âm");
            btnMic.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
        if (recorder != null) recorder.release();
    }
}
