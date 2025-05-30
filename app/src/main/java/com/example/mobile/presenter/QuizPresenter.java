package com.example.mobile.presenter;

import com.example.mobile.R;
import com.example.mobile.model.QuizModel;
import com.example.mobile.view.QuizView;

import java.util.ArrayList;
import java.util.List;

public class QuizPresenter {
    private QuizView view;
    private List<QuizModel> questions;
    private int currentQuestionIndex;

    public QuizPresenter(QuizView view) {
        this.view = view;
        this.questions = loadQuestions();
        this.currentQuestionIndex = 0;
        loadCurrentQuestion();
    }

    private List<QuizModel> loadQuestions() {
        List<QuizModel> list = new ArrayList<>();
        list.add(new QuizModel("What does the picture mean?",
                R.drawable.img_vocab_test,
                new String[]{"Mouth", "Eyes", "Ear", "Nose"},
                3)); // Nose is correct
        return list;
    }

    public void loadCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            QuizModel q = questions.get(currentQuestionIndex);
            view.resetView();
            view.showQuestion(q.getQuestionText(), q.getImageResId(), q.getOptions());
        }
    }

    public void handleAnswer(int selectedIndex) {
        QuizModel q = questions.get(currentQuestionIndex);
        boolean isCorrect = selectedIndex == q.getCorrectIndex();
        view.showFeedback(isCorrect, q.getOptions()[q.getCorrectIndex()]);
    }

    public void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            loadCurrentQuestion();
        } else {
            // No more questions - you can show a "Quiz Complete" dialog or return to menu
        }
    }
}
