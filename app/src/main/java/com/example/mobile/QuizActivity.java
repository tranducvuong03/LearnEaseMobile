package com.example.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.presenter.QuizPresenter;
import com.example.mobile.view.QuizView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * QuizActivity dùng layout activity_learning_vocab.xml
 * – Hai layout: initialAnswerLayout & feedbackLayout (dùng chung cho đúng / sai).
 * – 9 “dot” tiến trình (dot0 → dot8).
 * – Khi hoàn thành câu (đúng hoặc ấn Next) → finish() về màn trước.
 */
public class QuizActivity extends AppCompatActivity implements QuizView {

    /*-------------- Presenter ---------------*/
    private QuizPresenter presenter;

    /*-------------- View refs ---------------*/
    private TextView  questionText;
    private ImageView questionImage;

    private LinearLayout initialAnswerLayout;
    private LinearLayout feedbackLayout;

    private TextView feedbackTitle;
    private TextView feedbackAnswer;
    private Button   nextQuestionButton;

    private Button[] initialButtons;
    private View[]   progressDots;
    /*---------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_vocab);

        mapViews();
        mapProgressDots();

        /*--- Nhận dữ liệu Intent ---*/
        String word            = getIntent().getStringExtra("WORD");
        String distractorsJson = getIntent().getStringExtra("DISTRACTORS_JSON");
        List<String> distractors = new Gson().fromJson(
                distractorsJson, new TypeToken<List<String>>(){}.getType());

        presenter = new QuizPresenter(this, word, distractors, /*imageResId*/ -1);
    }

    /* ------------ Ánh xạ View ------------ */
    private void mapViews() {
        questionText  = findViewById(R.id.questionText);
        questionImage = findViewById(R.id.questionImage);

        initialAnswerLayout = findViewById(R.id.initialAnswerLayout);
        feedbackLayout      = findViewById(R.id.wrongFeedbackLayout);  // reuse
        feedbackTitle       = findViewById(R.id.wrongFeedbackTitle);
        feedbackAnswer      = findViewById(R.id.wrongFeedbackAnswer);
        nextQuestionButton  = findViewById(R.id.wrongNextQuestionButton);

        initialButtons = new Button[]{
                findViewById(R.id.buttonMouth),
                findViewById(R.id.buttonEyes),
                findViewById(R.id.buttonEar),
                findViewById(R.id.buttonNose)
        };
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            initialButtons[i].setOnClickListener(v -> presenter.handleAnswer(idx));
        }

        /*  Bấm Next => kết thúc QuizActivity  */
        nextQuestionButton.setOnClickListener(v -> finish());
    }

    /* ----------- Progress dots ----------- */
    private void mapProgressDots() {
        progressDots = new View[]{
                findViewById(R.id.dot0), findViewById(R.id.dot1), findViewById(R.id.dot2),
                findViewById(R.id.dot3), findViewById(R.id.dot4), findViewById(R.id.dot5),
                findViewById(R.id.dot6), findViewById(R.id.dot7), findViewById(R.id.dot8)
        };
    }
    private void updateProgress(int currentIndex) {
        for (int i = 0; i < progressDots.length; i++) {
            int res = (i <= currentIndex) ? R.drawable.blue_dot : R.drawable.gray_dot;
            progressDots[i].setBackgroundResource(res);
        }
    }

    /* ---------- QuizView impl ------------ */
    @Override
    public void showQuestion(String text, int imageResId, String[] options) {
        questionText.setText(text);
        if (imageResId != -1) {
            questionImage.setVisibility(View.VISIBLE);
            questionImage.setImageResource(imageResId);
        } else {
            questionImage.setVisibility(View.GONE);
        }
        for (int i = 0; i < 4; i++) initialButtons[i].setText(options[i]);
        updateProgress(0);           // luôn chỉ có 1 câu
    }

    @Override
    public void showFeedback(boolean isCorrect, String correctAnswer) {
        initialAnswerLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.VISIBLE);

        feedbackLayout.setBackgroundResource(
                isCorrect ? R.drawable.feedback_correct_background
                        : R.drawable.feedback_wrong_background);

        feedbackTitle.setText(isCorrect ? "Great job!" : "Oops… that's wrong");
        feedbackAnswer.setText("Answer: " + correctAnswer);

        /* Nếu trả lời đúng, auto đóng sau 1.2 s  */
        if (isCorrect) questionImage.postDelayed(this::finish, 1200);
    }

    @Override public void resetView() {
        initialAnswerLayout.setVisibility(View.VISIBLE);
        feedbackLayout.setVisibility(View.GONE);
    }

    @Override public void showCorrectAnswer(String s) { }
    @Override public void showWrongAnswer(String s)   { }
}
