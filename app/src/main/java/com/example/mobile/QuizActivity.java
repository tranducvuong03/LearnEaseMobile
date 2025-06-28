package com.example.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.model.VocabularyItem;
import com.example.mobile.presenter.QuizPresenter;
import com.example.mobile.presenter.QuizPresenter.Item;
import com.example.mobile.view.QuizView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements QuizView {

    private QuizPresenter presenter;

    private TextView questionText;
    private ImageView questionImage;

    private LinearLayout initialAnswerLayout;
    private LinearLayout feedbackLayout;
    private TextView feedbackTitle;
    private TextView feedbackAnswer;
    private Button nextQuestionButton;

    private Button[] answerBtns;
    private View[] progressDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_vocab);

        mapViews();
        mapProgressDots();

        List<Item> items = new ArrayList<>();

        // Nhận danh sách vocab (có distractorsJson dạng String)
        String questionsJson = getIntent().getStringExtra("QUESTIONS_JSON");
        if (questionsJson != null) {
            List<VocabularyItem> vocabList = new Gson().fromJson(
                    questionsJson,
                    new TypeToken<List<VocabularyItem>>() {}.getType()
            );

            for (VocabularyItem v : vocabList) {
                Item item = new Item();
                item.word = v.getWord();

                // Parse distractorsJson → List<String>
                List<String> distractors = new Gson().fromJson(
                        v.getDistractorsJson(),
                        new TypeToken<List<String>>() {}.getType()
                );
                item.distractors = distractors;

                item.imageResId = -1;
                items.add(item);
            }
        }

        presenter = new QuizPresenter(this, items);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void mapViews() {
        questionText = findViewById(R.id.questionText);
        questionImage = findViewById(R.id.questionImage);
        initialAnswerLayout = findViewById(R.id.initialAnswerLayout);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        feedbackTitle = findViewById(R.id.feedbackTitle);
        feedbackAnswer = findViewById(R.id.feedbackAnswer);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);

        answerBtns = new Button[]{
                findViewById(R.id.buttonMouth),
                findViewById(R.id.buttonEyes),
                findViewById(R.id.buttonEar),
                findViewById(R.id.buttonNose)
        };

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            answerBtns[i].setOnClickListener(v -> presenter.handleAnswer(idx));
        }

        nextQuestionButton.setOnClickListener(v -> presenter.nextQuestion());
    }

    private void mapProgressDots() {
        progressDots = new View[]{
                findViewById(R.id.dot0), findViewById(R.id.dot1), findViewById(R.id.dot2),
                findViewById(R.id.dot3), findViewById(R.id.dot4), findViewById(R.id.dot5),
                findViewById(R.id.dot6), findViewById(R.id.dot7), findViewById(R.id.dot8)
        };
    }

    private void updateProgress(int curIdx) {
        for (int i = 0; i < progressDots.length; i++) {
            progressDots[i].setBackgroundResource(
                    (i <= curIdx) ? R.drawable.blue_dot : R.drawable.gray_dot);
        }
    }

    @Override
    public void showQuestion(String text, int imageResId, String[] options, int curIndex) {
        questionText.setText(text);

        if (imageResId != -1) {
            questionImage.setVisibility(View.VISIBLE);
            questionImage.setImageResource(imageResId);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        for (int i = 0; i < 4; i++) {
            answerBtns[i].setText(options[i]);
        }

        updateProgress(curIndex);
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

        if (isCorrect) {
            questionImage.postDelayed(() -> presenter.nextQuestion(), 800);
        }
    }

    @Override
    public void resetView() {
        initialAnswerLayout.setVisibility(View.VISIBLE);
        feedbackLayout.setVisibility(View.GONE);
    }

    @Override
    public void finishQuiz() {
        setResult(RESULT_OK);
        finish();
    }
}
