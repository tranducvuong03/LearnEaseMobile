package com.example.mobile.view;

/**
 * Giao diện View cho module Quiz.
 * – Activity sẽ implement các hàm dưới đây.
 * – Presenter chỉ biết gọi các hàm này, không phụ thuộc vào chi tiết UI.
 */
public interface QuizView {

    /**
     * Hiển thị câu hỏi mới.
     *
     * @param questionText  văn bản câu hỏi (thường chính là từ vựng / prompt)
     * @param imageResId    id ảnh minh hoạ, truyền -1 nếu không có
     * @param options       4 đáp án đã được trộn
     * @param questionIndex chỉ số câu hỏi (0-based) để tô màu dot tiến trình
     */
    void showQuestion(String questionText,
                      int imageResId,
                      String[] options,
                      int questionIndex);

    /**
     * Hiển thị khung feedback sau khi người dùng chọn đáp án.
     *
     * @param isCorrect     true = đúng, false = sai
     * @param correctAnswer đáp án đúng (để hiển thị cho người dùng)
     */
    void showFeedback(boolean isCorrect, String correctAnswer);

    /** Ẩn khung feedback, đưa UI về trạng thái chuẩn bị cho câu kế tiếp. */
    void resetView();

    /** Gọi khi đã làm xong tất cả câu hỏi – Activity sẽ tự finish() hoặc điều hướng tuỳ ý. */
    void finishQuiz();
}
