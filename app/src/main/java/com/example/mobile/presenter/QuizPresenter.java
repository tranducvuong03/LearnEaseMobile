package com.example.mobile.presenter;

import com.example.mobile.view.QuizView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QuizPresenter: điều khiển logic cho một câu hỏi Vocabulary.
 * Hỗ trợ:
 *   – getCurrentQuestionIndex() để QuizActivity cập nhật tiến trình dot.
 */
public class QuizPresenter {

    private final QuizView view;
    private final String   correctWord;
    private final int      imageResId;
    private final String[] options;      // 4 đáp án đã shuffle
    private final int      correctIndex; // vị trí đáp án đúng (0‑3)

    // Index câu hiện tại (0-based) – cần cho progress dot
    private int currentQuestionIndex = 0; // presenter hiện chỉ có 1 câu nên luôn 0

    public QuizPresenter(QuizView view,
                         String word,
                         List<String> distractors,
                         int imageResId) {
        this.view        = view;
        this.correctWord = word;
        this.imageResId  = imageResId;

        // Ghép word + distractors → đủ 4 đáp án
        List<String> mix = new ArrayList<>();
        mix.add(word);
        if (distractors != null) mix.addAll(distractors);
        while (mix.size() < 4) mix.add("-");
        mix = mix.subList(0, 4);
        Collections.shuffle(mix);

        this.options      = mix.toArray(new String[0]);
        this.correctIndex = mix.indexOf(word);

        // Hiển thị câu hỏi ngay lập tức
        view.resetView();
        view.showQuestion(word, this.imageResId, this.options);
    }

    /** Người dùng chọn 1 đáp án (index 0‑3) */
    public void handleAnswer(int selectedIndex) {
        boolean isCorrect = (selectedIndex == correctIndex);
        view.showFeedback(isCorrect, correctWord);
    }

    /** Chuyển sang câu tiếp theo – demo: chỉ reset nhưng vẫn tăng index để dot cập nhật */
    public void nextQuestion() {
        currentQuestionIndex++;
        view.resetView();
    }

    /** Trả về chỉ số câu hiện tại để QuizActivity cập nhật progress dot */
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
}
