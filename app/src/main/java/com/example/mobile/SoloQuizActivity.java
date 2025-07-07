package com.example.mobile;


import android.Manifest;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mobile.api.LoginAPI;
import com.example.mobile.model.EvaluateLessonRequest;
import com.example.mobile.model.LessonPart;
import com.example.mobile.model.LessonResponse;
import com.example.mobile.model.QuizChoice;
import com.example.mobile.model.ScoreResponse;
import com.example.mobile.model.TranscriptionResponse;
import com.example.mobile.utils.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoloQuizActivity extends AppCompatActivity {
    private TextView promptText, referenceText;
    private Button playAudioBtn, submitBtn;
    private ImageButton recordBtn;
    private LinearLayout mcContainer;
    private LinearLayout layoutContainer;
    private LoginAPI apiService;
    private MediaRecorder recorder;
    private String audioPath;
    private LessonPart part;
    private String skill;
    private Map<String, String> selectedAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_quiz);

        promptText = findViewById(R.id.text_prompt);
        referenceText = findViewById(R.id.text_reference);
        playAudioBtn = findViewById(R.id.button_play_audio);
        recordBtn = findViewById(R.id.button_record_audio);
        submitBtn = findViewById(R.id.button_submit);
        mcContainer = findViewById(R.id.multiple_choice_container);
        layoutContainer = findViewById(R.id.layout_container);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        skill = getIntent().getStringExtra("skill");
        String lessonJson = getIntent().getStringExtra("lesson_json");
        UUID lessonId = UUID.fromString(getIntent().getStringExtra("lessonId"));
        LessonResponse lesson = new Gson().fromJson(lessonJson, LessonResponse.class);
        part = lesson.getParts().stream()
                .filter(p -> p.getSkill().equalsIgnoreCase(skill))
                .findFirst()
                .orElse(null);

        if (part == null) return;

        promptText.setText(part.getPrompt());

        switch (skill.toLowerCase()) {
            case "reading":
                setupReadingLayout();
                break;
            case "writing":
                setupWritingLayout();
                break;
            case "speaking":
                setupSpeakingLayout();
                break;
            case "listening":
                setupListeningLayout();
                break;
        }

        if (part.getReferenceText() != null && !skill.equalsIgnoreCase("listening")) {
            referenceText.setText(part.getReferenceText());
            referenceText.setVisibility(View.VISIBLE);
        } else {
            referenceText.setVisibility(View.GONE);
        }

        if (part.getAudioUrl() != null) {
            playAudioBtn.setVisibility(View.VISIBLE);
            playAudioBtn.setOnClickListener(v -> {
                MediaPlayer player = new MediaPlayer();
                player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                );

                try {
                    player.setDataSource(part.getAudioUrl()); // Đây là dòng gây IOException
                    player.setOnPreparedListener(MediaPlayer::start);
                    player.setOnErrorListener((mp, what, extra) -> {
                        Toast.makeText(this, "Không thể phát audio", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                    player.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi khi tải audio", Toast.LENGTH_SHORT).show();
                }
            });

        }

        if (part.getChoicesJson() != null) {
            Type listType = new TypeToken<List<QuizChoice>>() {}.getType();
            List<QuizChoice> questions = new Gson().fromJson(part.getChoicesJson(), listType);
            for (int i = 0; i < questions.size(); i++) {
                addQuestionToUI(i, questions.get(i));
            }
            submitBtn.setVisibility(View.VISIBLE);
            submitBtn.setOnClickListener(v -> evaluateAnswers());
        }
    }
    private void setupListeningLayout() {
        EditText writingInput = findViewById(R.id.editTextWritingAnswer);
        writingInput.setVisibility(View.VISIBLE);
        referenceText.setVisibility(View.GONE); // Ẩn đáp án cho đến khi submit

        submitBtn.setVisibility(View.VISIBLE);
        submitBtn.setOnClickListener(v -> {
            String answer = writingInput.getText().toString().trim();
            if (answer.isEmpty()) {
                Toast.makeText(this, "Bạn chưa nhập câu trả lời!", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedAnswers.clear();
            selectedAnswers.put("0", answer);

            evaluateAnswers(); // Gửi API và sẽ chuyển sang ReviewResultActivity
        });
    }

    private void setupReadingLayout() {
        referenceText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        referenceText.setPadding(32, 0, 32, 16);
        EditText writingInput = findViewById(R.id.editTextWritingAnswer);
        if (writingInput != null) {
            writingInput.setVisibility(View.GONE);
        }
    }

    private void setupWritingLayout() {
        promptText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        referenceText.setVisibility(View.GONE);

        EditText inputWriting = findViewById(R.id.editTextWritingAnswer); // Dùng EditText từ XML
        inputWriting.setVisibility(View.VISIBLE);
        submitBtn.setVisibility(View.VISIBLE);
        submitBtn.setOnClickListener(v -> {
            String answer = inputWriting.getText().toString().trim();
            if (answer.isEmpty()) {
                Toast.makeText(this, "Bạn chưa nhập bài viết!", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedAnswers.clear();
            selectedAnswers.put("0", answer);

            evaluateAnswers();
        });
    }

    private void setupSpeakingLayout() {
        recordBtn.setVisibility(View.VISIBLE);

        recordBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRecording();
                    return true;
            }
            return false;
        });

        layoutContainer.post(() -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutContainer.getLayoutParams();
            params.topMargin = (int) (getResources().getDisplayMetrics().heightPixels * 0.33);
            layoutContainer.setLayoutParams(params);
        });
    }

    private void startRecording() {
        audioPath = getExternalCacheDir().getAbsolutePath() + "/recording.mp3";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(audioPath);

        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;

                Toast.makeText(this, "Recording finished, sending...", Toast.LENGTH_SHORT).show();
                sendAudioToApi(audioPath);
            }
        } catch (RuntimeException e) {
            Toast.makeText(this, "Recording too short. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAudioToApi(String filePath) {
        File audioFile = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mp3"), audioFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("audioFile", audioFile.getName(), requestBody);

        apiService = RetrofitClient.getApiService(this);

        apiService.transcribeAudio(audioPart).enqueue(new Callback<TranscriptionResponse>() {
            @Override
            public void onResponse(Call<TranscriptionResponse> call, Response<TranscriptionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TranscriptionResponse.Result> results = response.body().getResults();
                    if (results != null && !results.isEmpty()) {
                        List<TranscriptionResponse.Alternative> alternatives = results.get(0).getAlternatives();
                        if (alternatives != null && !alternatives.isEmpty()) {
                            String spokenText = alternatives.get(0).getTranscript();
                            scoreSpeaking(spokenText); // So sánh với prompt
                            return;
                        }
                    }
                    Toast.makeText(SoloQuizActivity.this, "Không nhận được kết quả nhận diện", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SoloQuizActivity.this, "Không thể nhận diện âm thanh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TranscriptionResponse> call, Throwable t) {
                Toast.makeText(SoloQuizActivity.this, "Lỗi khi gửi âm thanh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scoreSpeaking(String spokenText) {
        String reference = part.getPrompt().trim().toLowerCase();
        String user = spokenText.trim().toLowerCase();

        String[] refWords = reference.split("\\s+");
        String[] userWords = user.split("\\s+");

        int match = 0;
        int max = Math.max(refWords.length, userWords.length);

        for (int i = 0; i < Math.min(refWords.length, userWords.length); i++) {
            if (refWords[i].equals(userWords[i])) match++;
        }

        float score = (100f * match) / max;
        String feedback;
        if (score >= 95) feedback = "🎯 Phát âm rất chuẩn!";
        else if (score >= 80) feedback = "👍 Phát âm khá tốt, cần cải thiện thêm.";
        else if (score >= 60) feedback = "📝 Bạn cần luyện tập thêm.";
        else feedback = "⚠️ Hãy thử nói rõ hơn và sát với đề bài.";

        showSpeakingResult(score, feedback);
    }
    private void showSpeakingResult(float score, String feedback) {
        Toast.makeText(this, "Score: " + score + "\n" + feedback, Toast.LENGTH_LONG).show();

        // Có thể chuyển sang ReviewResultActivity nếu muốn đồng bộ UI

    Intent intent = new Intent(SoloQuizActivity.this, ReviewResultActivity.class);
    intent.putExtra("score", score);
    intent.putExtra("feedback", feedback);
    startActivity(intent);
    finish();

    }


    private void addQuestionToUI(int index, QuizChoice q) {
        TextView questionText = new TextView(this);
        questionText.setText(q.getQuestion());
        questionText.setTextSize(16);
        questionText.setPadding(0, 16, 0, 4);
        mcContainer.addView(questionText);

        for (String choice : q.getChoices()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(choice);
            rb.setOnClickListener(v -> selectedAnswers.put(String.valueOf(index), getAnswerLetter(choice, q.getChoices())));
            mcContainer.addView(rb);
        }
    }

    private String getAnswerLetter(String choice, List<String> choices) {
        int index = choices.indexOf(choice);
        return String.valueOf((char) ('A' + index));
    }
    private String getUserIdFromPrefs() {
        return getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("user_id", "");
    }
    private void evaluateAnswers() {
        LoginAPI api = RetrofitClient.getApiService(this);
        UUID userId = UUID.fromString(getUserIdFromPrefs());
        UUID lessonId = UUID.fromString(getIntent().getStringExtra("lessonId"));

        Map<String, String> answerMap = new HashMap<>(selectedAnswers);
        EvaluateLessonRequest request = new EvaluateLessonRequest(userId, lessonId, skill, answerMap);

        com.example.mobile.utils.LoadingManager.getInstance().show(SoloQuizActivity.this);

        if (skill.equalsIgnoreCase("listening")) {
            api.evaluateListening(request).enqueue(new Callback<ScoreResponse>() {
                @Override
                public void onResponse(Call<ScoreResponse> call, Response<ScoreResponse> response) {
                    com.example.mobile.utils.LoadingManager.getInstance().dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(SoloQuizActivity.this, ReviewResultActivity.class);
                            intent.putExtra("userId", userId.toString());
                            intent.putExtra("lessonId", lessonId.toString());
                            intent.putExtra("skill", skill);
                            startActivity(intent);
                            finish();
                        }, 800);
                    } else {
                        Toast.makeText(SoloQuizActivity.this, "Đánh giá Listening thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ScoreResponse> call, Throwable t) {
                    com.example.mobile.utils.LoadingManager.getInstance().dismiss();
                    Toast.makeText(SoloQuizActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (skill.equalsIgnoreCase("writing")) {
            api.evaluateWritingAnswer(request).enqueue(new Callback<ScoreResponse>() {
                @Override
                public void onResponse(Call<ScoreResponse> call, Response<ScoreResponse> response) {
                    com.example.mobile.utils.LoadingManager.getInstance().dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(SoloQuizActivity.this, ReviewResultActivity.class);
                            intent.putExtra("userId", userId.toString());
                            intent.putExtra("lessonId", lessonId.toString());
                            intent.putExtra("skill", skill);
                            startActivity(intent);
                            finish();
                        }, 800);
                    } else {
                        Toast.makeText(SoloQuizActivity.this, "Đánh giá Writing thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ScoreResponse> call, Throwable t) {
                    com.example.mobile.utils.LoadingManager.getInstance().dismiss();
                    Toast.makeText(SoloQuizActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            api.evaluateLesson(request).enqueue(new Callback<ScoreResponse>() {
                @Override
                public void onResponse(Call<ScoreResponse> call, Response<ScoreResponse> response) {
                    com.example.mobile.utils.LoadingManager.getInstance().dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(SoloQuizActivity.this, ReviewResultActivity.class);
                            intent.putExtra("userId", userId.toString());
                            intent.putExtra("lessonId", lessonId.toString());
                            intent.putExtra("skill", skill);
                            startActivity(intent);
                            finish();
                        }, 800);
                    } else {
                        Toast.makeText(SoloQuizActivity.this, "Evaluation failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ScoreResponse> call, Throwable t) {
                    com.example.mobile.utils.LoadingManager.getInstance().dismiss();
                    Toast.makeText(SoloQuizActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}


