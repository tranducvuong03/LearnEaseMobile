package com.example.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.QuizPresenter;
import com.example.mobile.QuizPresenter.Item;
import com.example.mobile.view.QuizView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * QuizActivity – chơi nhiều câu hỏi liên tiếp.
 * • layout: activity_learning_vocab.xml (đã có các id dot0 → dot8).
 * • Hai phần chính: initialAnswerLayout & feedbackLayout.
 */
public class QuizActivity extends AppCompatActivity implements QuizView {

    /*----------- Presenter -----------*/
    private QuizPresenter presenter;

    /*----------- View refs -----------*/
    private TextView  questionText;
    private ImageView questionImage;

    private LinearLayout initialAnswerLayout;
    private LinearLayout feedbackLayout;      // dùng chung đúng / sai
    private TextView  feedbackTitle;
    private TextView  feedbackAnswer;
    private Button    nextQuestionButton;

    private Button[] answerBtns;
    private View[]   progressDots;
    /*---------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_vocab);

        mapViews();
        mapProgressDots();

        /*-------- Nhận danh sách câu hỏi từ Intent --------*/
        // Nếu chỉ truyền 1 câu: WORD + DISTRACTORS_JSON
        // Nếu truyền nhiều câu: QUESTIONS_JSON (array Item)
        List<Item> items;

        String questionsJson = getIntent().getStringExtra("QUESTIONS_JSON");
        if (questionsJson != null) {                       // nhiều câu
            items = new Gson().fromJson(
                    questionsJson,
                    new TypeToken<List<Item>>(){}.getType());
        } else {                                           // 1 câu
            items = new ArrayList<>();
            String word   = getIntent().getStringExtra("WORD");
            String distJs = getIntent().getStringExtra("DISTRACTORS_JSON");
            List<String> distractors = new Gson().fromJson(
                    distJs, new TypeToken<List<String>>(){}.getType());

            Item it      = new Item();
            it.word      = word;
            it.distractors = distractors;
            it.imageResId  = -1;
            items.add(it);
        }

        /*-------- Khởi tạo presenter --------*/
        presenter = new QuizPresenter(this, items);
    }

    /*======================== MAP VIEW ========================*/
    private void mapViews() {
        questionText  = findViewById(R.id.questionText);
        questionImage = findViewById(R.id.questionImage);

        initialAnswerLayout = findViewById(R.id.initialAnswerLayout);
        feedbackLayout      = findViewById(R.id.wrongFeedbackLayout);
        feedbackTitle       = findViewById(R.id.wrongFeedbackTitle);
        feedbackAnswer      = findViewById(R.id.wrongFeedbackAnswer);
        nextQuestionButton  = findViewById(R.id.wrongNextQuestionButton);

        answerBtns = new Button[] {
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
        progressDots = new View[] {
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
    /*==========================================================*/

    /*===================== QuizView impl ======================*/
    @Override
    public void showQuestion(String text, int imageResId,
                             String[] options, int curIndex) {

        questionText.setText(text);
        if (imageResId != -1) {
            questionImage.setVisibility(View.VISIBLE);
            questionImage.setImageResource(imageResId);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        for (int i = 0; i < 4; i++) answerBtns[i].setText(options[i]);

        updateProgress(curIndex);
    }

    @Override
    public void showFeedback(boolean isCorrect, String correctAnswer) {
        initialAnswerLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.VISIBLE);

        feedbackLayout.setBackgroundResource(
                isCorrect ? R.drawable.feedback_correct_background
                        : R.drawable.feedback_wrong_background);

        feedbackTitle .setText(isCorrect ? "Great job!" : "Oops… that's wrong");
        feedbackAnswer.setText("Answer: " + correctAnswer);

        if (isCorrect) questionImage.postDelayed(() -> presenter.nextQuestion(), 800);
    }

    @Override public void resetView() {
        initialAnswerLayout.setVisibility(View.VISIBLE);
        feedbackLayout     .setVisibility(View.GONE);
    }

    @Override public void finishQuiz() {
        setResult(RESULT_OK);
        finish();
    }

}
