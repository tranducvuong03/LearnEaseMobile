package com.example.mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
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

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.SimpleExoPlayer;

import com.example.mobile.api.CompareSpeakingAPI;
import com.example.mobile.model.SpeakingExercise;
import com.example.mobile.utils.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@UnstableApi
public class SpeakingLessonActivity extends AppCompatActivity {

    private static final int REQ_AUDIO_PERM = 200;

    private TextView tvPhrase, tvHold, tvScore;
    private ImageButton btnMic;
    private Button btnNext;
    private LinearLayout layoutAudio;
    private SimpleExoPlayer exoPlayer;
    private MediaRecorder recorder;
    private File userAudioFile;

    private List<SpeakingExercise> exerciseList;
    private int currentIndex = 0;

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
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String json = getIntent().getStringExtra("SPEAKING_LIST");
        exerciseList = new Gson().fromJson(json, new TypeToken<List<SpeakingExercise>>() {}.getType());

        if (exerciseList == null || exerciseList.isEmpty()) {
            showToast("Không có dữ liệu bài nói");
            finish();
            return;
        }

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

        btnNext.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < exerciseList.size()) {
                showCurrentExercise();
            } else {
                setResult(RESULT_OK);
                finish();
            }
        });

        showCurrentExercise();
    }

    private void showCurrentExercise() {
        tvScore.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        tvHold.setText("Hold To Pronounce");

        SpeakingExercise ex = exerciseList.get(currentIndex);
        tvPhrase.setText("'" + ex.getPrompt() + "'");
        layoutAudio.setOnClickListener(v -> playSampleAudio(ex.getSampleAudioUrl()));
    }

    private void startRecording() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (dir != null && !dir.exists()) dir.mkdirs();
            userAudioFile = new File(dir, "user_" + System.currentTimeMillis() + ".m4a");

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
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

            String sampleUrl = exerciseList.get(currentIndex).getSampleAudioUrl();
            uploadAndCompare(userAudioFile, sampleUrl);
        } catch (Exception ex) {
            Log.e("Recorder", "stopRecording", ex);
        }
    }

    private void uploadAndCompare(File userFile, String sampleUrl) {
        CompareSpeakingAPI api = RetrofitClient.getCompareApiService(this);

        RequestBody audioBody = RequestBody.create(userFile, MediaType.parse("audio/mp4"));
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
                showResult(resp.body().similarity);
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

    private void playSampleAudio(String sampleAudioUrl) {
        if (sampleAudioUrl == null || sampleAudioUrl.isEmpty()) {
            showToast("Không có audio mẫu");
            return;
        }

        try {
            if (exoPlayer != null) {
                exoPlayer.release();
            }

            exoPlayer = new SimpleExoPlayer.Builder(this).build();
            MediaItem mediaItem = MediaItem.fromUri(sampleAudioUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();

        } catch (Exception ex) {
            showToast("Không thể phát audio: " + ex.getMessage());
            Log.e("ExoPlayer", "playSampleAudio", ex);
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
        if (exoPlayer != null) exoPlayer.release();
        if (recorder != null) recorder.release();
    }
}
