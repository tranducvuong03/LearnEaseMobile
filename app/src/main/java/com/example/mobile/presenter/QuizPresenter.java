package com.example.mobile;

import com.example.mobile.view.QuizView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter cho màn Quiz (MVP pattern – độc lập UI).
 *
 * – Mỗi {@link Item} tương ứng 1 câu hỏi Vocabulary.
 * – Không truy cập trực tiếp tới View implementation (không cast sang Activity).
 */
public class QuizPresenter {

    /*===================== DTO =====================*/
    public static class Item {
        public String word;              // đáp án đúng
        public List<String> distractors; // tối đa 3 đáp án nhiễu
        public int imageResId = -1;      // -1 nếu không có
    }
    /*================================================*/

    private final QuizView view;
    private final List<Item> items;

    private int currentIndex = 0;       // chỉ số câu hiện tại
    private String[] currentOptions;    // 4 đáp án của câu hiện tại
    private int      correctIndex;      // vị trí đáp án đúng trong currentOptions

    /*-------------- CONSTRUCTOR ----------------------------------*/
    public QuizPresenter(QuizView view, List<Item> items) {
        this.view  = view;
        this.items = items != null ? items : new ArrayList<>();

        loadCurrentQuestion();
    }

    /*============== API cho View ==================*/
    public int getCurrentQuestionIndex() {
        return currentIndex;
    }

    /** Được View gọi khi user chọn đáp án <code>chosenIdx</code> (0-3). */
    public void handleAnswer(int chosenIdx) {
        boolean isCorrect = (chosenIdx == correctIndex);
        view.showFeedback(isCorrect, currentOptions[correctIndex]);
    }

    /** View gọi khi bấm “Next question”. */
    public void nextQuestion() {
        currentIndex++;
        loadCurrentQuestion();
    }
    /*=============================================*/

    /*-------------- LOGIC PRIVATE ----------------*/
    private void loadCurrentQuestion() {
        if (currentIndex >= items.size()) {
            // Hết câu hỏi → thông báo cho View đóng quiz
            view.finishQuiz();
            return;
        }

        Item it = items.get(currentIndex);

        /*--- Tạo danh sách 4 đáp án ---*/
        List<String> opts = new ArrayList<>();
        opts.add(it.word);
        if (it.distractors != null) opts.addAll(it.distractors);
        while (opts.size() < 4) opts.add("-");          // đủ 4
        opts = opts.subList(0, 4);                      // an toàn
        Collections.shuffle(opts);

        /* Lưu lại để so sánh ở handleAnswer() */
        currentOptions = opts.toArray(new String[0]);
        correctIndex   = opts.indexOf(it.word);

        /* Gửi câu hỏi cho View */
        view.resetView();
        view.showQuestion(it.word,
                it.imageResId,
                currentOptions,
                currentIndex);  // để tô dot tiến trình
    }
    /*-------------------------------------------------------------*/
}
