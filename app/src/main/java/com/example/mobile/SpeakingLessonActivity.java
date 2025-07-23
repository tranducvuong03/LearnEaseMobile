package com.example.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.example.mobile.api.LearningAPI;
import com.example.mobile.model.EvaluateSpeakingResponse;
import com.example.mobile.model.LessonProgress;
import com.example.mobile.model.SpeakingExercise;
import com.example.mobile.model.SubmitProgressRequest;
import com.example.mobile.utils.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private FrameLayout backButtonCard;
    private LinearLayout progressDots;
    private MediaRecorder recorder;
    private File userAudioFile;
    private SimpleExoPlayer exoPlayer;
    private boolean isRecording = false;

    private List<SpeakingExercise> exerciseList;
    private int currentIndex = 0;
    private LessonProgress lessonProgress;
    private CompareSpeakingAPI compareApi;
    private LearningAPI learningApi;
    private String userId;

    // offset passed from QuizActivity to continue progress dots
    private int dotOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_test);

        mapViews();
        setupPermissions();
        initApis();
        loadIntentData();

        // read offset for progress dots from quiz
        dotOffset = getIntent().getIntExtra("dotOffset", 0);

        if (exerciseList == null || exerciseList.isEmpty()) {
            showToast("No speaking exercises available");
            finish();
            return;
        }

        // record on touch
        /*btnMic.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRecordingAndEvaluate();
                    return true;
            }
            return false;
        });*/
        btnMic.setOnClickListener(v -> testEvaluate());

        btnNext.setOnClickListener(v -> nextExercise());
        loadExercise();
    }

    private void mapViews() {
        tvPhrase = findViewById(R.id.tvPhrase);
        tvHold = findViewById(R.id.tvHold);
        tvScore = findViewById(R.id.tvScore);
        btnMic = findViewById(R.id.btnMic);
        btnNext = findViewById(R.id.btnNext);
        layoutAudio = findViewById(R.id.layoutAudio);
        backButtonCard = findViewById(R.id.backButtonCard);
        progressDots = findViewById(R.id.progressDots);

        backButtonCard.setOnClickListener(v -> finish());
    }

    private void setupPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQ_AUDIO_PERM);
        }
    }

    private void initApis() {
        compareApi = RetrofitClient.getCompareApiService(this);
        learningApi = RetrofitClient.getLearningApi(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);
    }

    private void loadIntentData() {
        String json = getIntent().getStringExtra("speaking_list");
        exerciseList = new Gson().fromJson(json,
                new TypeToken<List<SpeakingExercise>>() {}.getType());
        String progressJson = getIntent().getStringExtra("lesson_progress");
        lessonProgress = new Gson().fromJson(progressJson, LessonProgress.class);
    }

    private void loadExercise() {
        tvScore.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        tvHold.setText("Hold To Pronounce");

        updateProgressDots();

        SpeakingExercise ex = exerciseList.get(currentIndex);
        tvPhrase.setText("'" + ex.getPrompt() + "'");
        layoutAudio.setOnClickListener(v -> playSampleAudio(ex.getSampleAudioUrl()));
    }

    private void updateProgressDots() {
        int total = progressDots.getChildCount();
        int completed = dotOffset + currentIndex;
        for (int i = 0; i < total; i++) {
            View dot = progressDots.getChildAt(i);
            int res = (i <= completed) ? R.drawable.blue_dot : R.drawable.gray_dot;
            dot.setBackgroundResource(res);
        }
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
            isRecording = true;
            tvHold.setText("Recording…");
        } catch (IOException ex) {
            Log.e("Recorder", "startRecording", ex);
            showToast("Unable to record audio: " + ex.getMessage());
        }
    }

    private void stopRecordingAndEvaluate() {
        if (recorder != null) {
            try {
                if (isRecording) {
                    recorder.stop();
                }
            } catch (RuntimeException e) {
                Log.e("Recorder", "stop failed", e);
                if (userAudioFile != null && userAudioFile.exists()) {
                    userAudioFile.delete();
                }
            } finally {
                recorder.reset();
                recorder.release();
                recorder = null;
                isRecording = false;
            }
        }
        tvHold.setText("Scoring...");
        evaluateSpeaking();
    }

    private void testEvaluate() {
        userAudioFile = getTestAudioFile();
        tvHold.setText("Testing audio...");
        evaluateSpeaking();
    }

    private void evaluateSpeaking() {
        SpeakingExercise ex = exerciseList.get(currentIndex);
        RequestBody audioPart = RequestBody.create(userAudioFile,
                MediaType.parse("audio/mp4"));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "AudioFile", userAudioFile.getName(), audioPart);
        RequestBody promptPart = RequestBody.create(ex.getPrompt(),
                MediaType.parse("text/plain"));

        compareApi.evaluateSpeaking(filePart, promptPart)
                .enqueue(new Callback<EvaluateSpeakingResponse>() {
                    @Override
                    public void onResponse(Call<EvaluateSpeakingResponse> call,
                                           Response<EvaluateSpeakingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int score = response.body().getScore();
                            showScore(score, response.body().getFeedback());
                            submitProgress(score >= 70);
                        } else showError();
                    }

                    @Override
                    public void onFailure(Call<EvaluateSpeakingResponse> call, Throwable t) {
                        showError();
                    }
                });
    }

    private File getTestAudioFile() {
        File out = new File(getCacheDir(), "test_audio.mp3");
        try (InputStream in = getResources().openRawResource(R.raw.economic__us_2_rr);
             FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e("TestAudio", "copy failed", e);
        }
        return out;
    }

    private void submitProgress(boolean isCorrect) {
        SpeakingExercise ex = exerciseList.get(currentIndex);
        SubmitProgressRequest req = new SubmitProgressRequest(
                lessonProgress.getLessonId(), null, ex.getExerciseId(), isCorrect);

        learningApi.submitProgress(userId, req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {}

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("API", "submitProgress failed", t);
                    }
                });
    }

    private void showScore(int score, String feedback) {
        tvScore.setVisibility(View.VISIBLE);
        tvScore.setText(String.format(Locale.getDefault(), "Score: %d", score));
        tvHold.setText(feedback);
        btnNext.setVisibility(score >= 70 ? View.VISIBLE : View.GONE);
    }

    private void showError() {
        tvHold.setText("Hold To Pronounce");
        showToast("API error, please try again");
    }

    private void nextExercise() {
        currentIndex++;
        if (currentIndex < exerciseList.size()) {
            loadExercise();
        } else {
            lessonProgress.checkCompletion();
            showToast(lessonProgress.isCompleted() ?
                    "You’ve completed the lesson!" : "You need more practice to complete the lesson.");

            // Khởi động màn hình hoàn thành
            Intent intent = new Intent(SpeakingLessonActivity.this, CompletionActivity.class);
            startActivity(intent);

            finish();
        }
    }

    private void playSampleAudio(String url) {
        stopExoPlayer();
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        exoPlayer.setMediaItem(MediaItem.fromUri(url));
        exoPlayer.prepare();
        exoPlayer.play();
    }

    private void stopExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_AUDIO_PERM &&
                (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            showToast("Recording permission denied");
            btnMic.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) recorder.release();
        stopExoPlayer();
    }
}
