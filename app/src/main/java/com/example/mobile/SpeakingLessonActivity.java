package com.example.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class SpeakingLessonActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private TextView tvPrompt;
    private TextView tvPhrase;
    private ImageButton btnBack;
    private ImageButton btnMic;
    private TextView tvHold;
    private LinearLayout layoutAudio;
    private MediaPlayer mediaPlayer;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private String prompt;
    private String sampleAudioUrl;
    private UUID exerciseId;
    private UUID dialectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaking_test);

        tvPrompt = findViewById(R.id.tvPrompt);
        tvPhrase = findViewById(R.id.tvPhrase);
        btnBack = findViewById(R.id.btnBack);
        btnMic = findViewById(R.id.btnMic);
        tvHold = findViewById(R.id.tvHold);
        layoutAudio = findViewById(R.id.layoutAudio);

        if (getIntent() != null) {
            String exerciseIdString = getIntent().getStringExtra("EXERCISE_ID");
            if (exerciseIdString != null) {
                try {
                    exerciseId = UUID.fromString(exerciseIdString);
                } catch (IllegalArgumentException e) {
                    Log.e("SpeakingLessonActivity", "Invalid EXERCISE_ID UUID: " + exerciseIdString, e);
                    Toast.makeText(this, "Lỗi dữ liệu bài tập", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            } else {
                Log.e("SpeakingLessonActivity", "EXERCISE_ID is null. Finishing activity.");
                Toast.makeText(this, "Không tìm thấy ID bài tập.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            prompt = getIntent().getStringExtra("PROMPT");
            sampleAudioUrl = getIntent().getStringExtra("SAMPLE_AUDIO_URL");
            String dialectIdString = getIntent().getStringExtra("DIALECT_ID");
            if (dialectIdString != null) {
                try {
                    dialectId = UUID.fromString(dialectIdString);
                } catch (IllegalArgumentException e) {
                    Log.e("SpeakingLessonActivity", "Invalid DIALECT_ID UUID: " + dialectIdString, e);
                }
            }

            if (prompt != null) {
                tvPhrase.setText("'" + prompt + "'");
            } else {
                tvPhrase.setText("N/A");
                Log.w("SpeakingLessonActivity", "PROMPT is null for Speaking exercise.");
            }

            Log.d("SpeakingLessonActivity", "Exercise ID: " + exerciseId);
            Log.d("SpeakingLessonActivity", "Prompt: " + prompt);
            Log.d("SpeakingLessonActivity", "Sample Audio URL: " + sampleAudioUrl);
            Log.d("SpeakingLessonActivity", "Dialect ID: " + dialectId);
        } else {
            Toast.makeText(this, "Không có dữ liệu bài học.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> onBackPressed());
        layoutAudio.setOnClickListener(v -> playSampleAudio());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            setupSpeechRecognizer();
        }

        btnMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Đảm bảo SpeechRecognizer đã sẵn sàng trước khi bắt đầu lắng nghe
                        if (speechRecognizer == null) {
                            setupSpeechRecognizer(); // Tạo lại nếu đã bị hủy
                        }
                        startListening();
                        tvHold.setText("Listening...");
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopListening();
                        tvHold.setText("Hold To Pronounce");
                        return true;
                }
                return false;
            }
        });

        mediaPlayer = new MediaPlayer();
    }

    private void setupSpeechRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy(); // Hủy instance cũ nếu có
            speechRecognizer = null;
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói.", Toast.LENGTH_LONG).show();
            Log.e("SpeakingLessonActivity", "Speech Recognition is not available on this device.");
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { Log.d("SpeechRecognizer", "onReadyForSpeech"); }
            @Override
            public void onBeginningOfSpeech() { Log.d("SpeechRecognizer", "onBeginningOfSpeech"); }
            @Override
            public void onRmsChanged(float rmsdB) { }
            @Override
            public void onBufferReceived(byte[] buffer) { Log.d("SpeechRecognizer", "onBufferReceived"); }
            @Override
            public void onEndOfSpeech() {
                Log.d("SpeechRecognizer", "onEndOfSpeech");
                // Sau khi kết thúc lời nói, hủy SpeechRecognizer để nó được giải phóng hoàn toàn.
                // Nó sẽ được tạo lại khi người dùng nhấn nút lần nữa.
                if (speechRecognizer != null) {
                    speechRecognizer.destroy();
                    speechRecognizer = null;
                    Log.d("SpeechRecognizer", "SpeechRecognizer destroyed after onEndOfSpeech.");
                }
            }

            @Override
            public void onError(int error) {
                String errorMessage = getErrorText(error);
                Log.e("SpeechRecognizer", "Recognition error: " + errorMessage + " (" + error + ")");
                Toast.makeText(SpeakingLessonActivity.this, "Lỗi nhận diện: " + errorMessage, Toast.LENGTH_LONG).show();
                tvHold.setText("Hold To Pronounce");

                // Rất quan trọng: Hủy SpeechRecognizer khi có lỗi để tránh lỗi liên tiếp.
                if (speechRecognizer != null) {
                    speechRecognizer.destroy();
                    speechRecognizer = null;
                    Log.e("SpeechRecognizer", "SpeechRecognizer destroyed due to error.");
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    Log.d("SpeechRecognizer", "Recognized Text: " + recognizedText);
                    Toast.makeText(SpeakingLessonActivity.this, "Bạn đã nói: " + recognizedText, Toast.LENGTH_LONG).show();
                    compareSpeechToPrompt(recognizedText, prompt);
                } else {
                    Toast.makeText(SpeakingLessonActivity.this, "Không nhận diện được giọng nói.", Toast.LENGTH_SHORT).show();
                }
                tvHold.setText("Hold To Pronounce");

                // Rất quan trọng: Hủy SpeechRecognizer sau khi có kết quả.
                if (speechRecognizer != null) {
                    speechRecognizer.destroy();
                    speechRecognizer = null;
                    Log.d("SpeechRecognizer", "SpeechRecognizer destroyed after onResults.");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) { }
            @Override
            public void onEvent(int eventType, Bundle params) { Log.d("SpeechRecognizer", "onEvent: " + eventType); }
        });

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    private void startListening() {
        if (speechRecognizer != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
            Toast.makeText(this, "Bắt đầu ghi âm...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi: Bộ nhận diện giọng nói chưa sẵn sàng. Đang thử thiết lập lại.", Toast.LENGTH_SHORT).show();
            setupSpeechRecognizer(); // Thử thiết lập lại nếu null
            if (speechRecognizer != null) { // Nếu thiết lập lại thành công, thử lắng nghe
                speechRecognizer.startListening(speechRecognizerIntent);
            } else {
                Toast.makeText(this, "Không thể khởi tạo Bộ nhận diện giọng nói.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            Toast.makeText(this, "Dừng ghi âm.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO: return "Lỗi ghi âm.";
            case SpeechRecognizer.ERROR_CLIENT: return "Lỗi client (có thể do lỗi mạng hoặc cấu hình).";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Không có quyền ghi âm.";
            case SpeechRecognizer.ERROR_NETWORK: return "Lỗi mạng.";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Mạng quá thời gian.";
            case SpeechRecognizer.ERROR_NO_MATCH: return "Không tìm thấy kết quả.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "Bộ nhận diện đang bận.";
            case SpeechRecognizer.ERROR_SERVER: return "Lỗi từ máy chủ nhận diện.";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "Không có đầu vào giọng nói.";
            default: return "Lỗi không xác định.";
        }
    }

    private void compareSpeechToPrompt(String recognizedText, String targetPrompt) {
        if (targetPrompt == null || targetPrompt.isEmpty()) {
            Toast.makeText(this, "Không có cụm từ mục tiêu để so sánh.", Toast.LENGTH_SHORT).show();
            return;
        }

        String cleanedRecognized = recognizedText.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(Locale.getDefault());
        String cleanedTarget = targetPrompt.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(Locale.getDefault());

        if (cleanedRecognized.equals(cleanedTarget)) {
            Toast.makeText(this, "Chính xác! Phát âm đúng.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Chưa chính xác. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
        }
    }

    private void playSampleAudio() {
        if (sampleAudioUrl == null || sampleAudioUrl.isEmpty()) {
            Toast.makeText(this, "Không có file âm thanh mẫu.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(this, Uri.parse(sampleAudioUrl));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(SpeakingLessonActivity.this, "Đang phát âm thanh...", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(SpeakingLessonActivity.this, "Phát âm thanh hoàn tất.", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("SpeakingLessonActivity", "MediaPlayer error: " + what + ", " + extra);
                Toast.makeText(SpeakingLessonActivity.this, "Lỗi khi phát âm thanh.", Toast.LENGTH_SHORT).show();
                return false;
            });
        } catch (IOException | IllegalArgumentException | SecurityException | IllegalStateException e) {
            Log.e("SpeakingLessonActivity", "Error playing sample audio: " + e.getMessage(), e);
            Toast.makeText(this, "Không thể phát âm thanh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSpeechRecognizer();
            } else {
                Toast.makeText(this, "Quyền ghi âm bị từ chối. Không thể sử dụng chức năng mic.", Toast.LENGTH_LONG).show();
                Log.w("SpeakingLessonActivity", "RECORD_AUDIO permission denied.");
                btnMic.setEnabled(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}