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

public class QuizActivity extends AppCompatActivity implements QuizView {
    private QuizPresenter presenter;

    private TextView questionText;
    private ImageView questionImage;

    private LinearLayout initialAnswerLayout, feedbackLayout, wrongFeedbackLayout;
    private TextView feedbackTitle, feedbackAnswer, wrongFeedbackTitle, wrongFeedbackAnswer;
    private Button nextQuestionButton, wrongNextQuestionButton;

    private Button[] initialButtons;
    private Button[] feedbackButtons;
    private Button[] wrongButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_vocab);

        presenter = new QuizPresenter(this);
        /*mapViews();*/
    }

    /*private void mapViews() {
        questionText = findViewById(R.id.questionText);
        questionImage = findViewById(R.id.questionImage);

        initialAnswerLayout = findViewById(R.id.initialAnswerLayout);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        wrongFeedbackLayout = findViewById(R.id.wrongFeedbackLayout);

        feedbackTitle = findViewById(R.id.feedbackTitle);
        feedbackAnswer = findViewById(R.id.feedbackAnswer);
        wrongFeedbackTitle = findViewById(R.id.wrongFeedbackTitle);
        wrongFeedbackAnswer = findViewById(R.id.wrongFeedbackAnswer);

        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        wrongNextQuestionButton = findViewById(R.id.wrongNextQuestionButton);

        // Khởi tạo mảng 4 nút
        initialButtons = new Button[]{
                findViewById(R.id.buttonMouth),
                findViewById(R.id.buttonEyes),
                findViewById(R.id.buttonEar),
                findViewById(R.id.buttonNose)
        };

        feedbackButtons = new Button[]{
                findViewById(R.id.buttonMouthFeedback),
                findViewById(R.id.buttonEyesFeedback),
                findViewById(R.id.buttonEarFeedback),
                findViewById(R.id.buttonNoseFeedback)
        };

        wrongButtons = new Button[]{
                findViewById(R.id.buttonMouthWrong),
                findViewById(R.id.buttonEyesWrong),
                findViewById(R.id.buttonEarWrong),
                findViewById(R.id.buttonNoseWrong)
        };

        for (int i = 0; i < 4; i++) {
            final int index = i;
            initialButtons[i].setOnClickListener(v -> presenter.handleAnswer(index));
        }

        nextQuestionButton.setOnClickListener(v -> presenter.nextQuestion());
        wrongNextQuestionButton.setOnClickListener(v -> presenter.nextQuestion());
    }
*/
    @Override
    public void showQuestion(String question, int imageResId, String[] options) {
        questionText.setText(question);
        questionImage.setImageResource(imageResId);

        for (int i = 0; i < 4; i++) {
            initialButtons[i].setText(options[i]);
            feedbackButtons[i].setText(options[i]);
            wrongButtons[i].setText(options[i]);
        }
    }

    @Override
    public void showFeedback(boolean isCorrect, String correctAnswer) {
        initialAnswerLayout.setVisibility(View.GONE);
        if (isCorrect) {
            feedbackLayout.setVisibility(View.VISIBLE);
            feedbackTitle.setText("Amazing!");
            feedbackAnswer.setText("Answer: " + correctAnswer);
        } else {
            wrongFeedbackLayout.setVisibility(View.VISIBLE);
            wrongFeedbackTitle.setText("Oops... that's wrong");
            wrongFeedbackAnswer.setText("Answer: " + correctAnswer);
        }
    }

    @Override
    public void showCorrectAnswer(String correctAnswer) {
        // Optional: Show correct answer UI (merged with showFeedback here)
    }

    @Override
    public void showWrongAnswer(String correctAnswer) {
        // Optional: Show wrong answer UI (merged with showFeedback here)
    }

    @Override
    public void resetView() {
        initialAnswerLayout.setVisibility(View.VISIBLE);
        feedbackLayout.setVisibility(View.GONE);
        wrongFeedbackLayout.setVisibility(View.GONE);
    }
}
