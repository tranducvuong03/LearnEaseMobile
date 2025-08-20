package com.example.mobile.reminders;

import java.util.Calendar;

public class ReminderMessages {
    private static final String[] TITLES = {
            "Đừng bỏ lỡ streak 🔥",
            "Một chút nữa là xong! 💪",
            "Bạn sẽ thấy tự hào 😌",
            "Hôm nay mình đã học chưa? 🤔",
            "Nhắc nhẹ nè ✨",
            "Giữ lửa nào! 🔥",
            "Tiến bộ từng ngày 📈",
            "Chạm mục tiêu nhé 🎯",
            "Bước nhỏ – kết quả lớn 🚀",
            "Bạn làm được mà! 🙌",
    };

    private static final String[] BODIES = {
            "Bạn chưa credit streak hôm nay. Vào học để giữ streak nhé!",
            "Thêm chút nữa là trọn ngày đẹp rồi. Vào bài nhanh nào!",
            "Một phiên ngắn thôi cũng đủ giữ streak. Bắt đầu ngay!",
            "Chỉ cần hoàn thành challenge hoặc đủ 25 phút là xong. Cố lên!",
            "Thói quen nhỏ mỗi ngày tạo nên khác biệt lớn.",
            "Giữ chuỗi ngày tuyệt vời của bạn tiếp tục nào!",
            "Tích luỹ đều mỗi ngày – tương lai cảm ơn bạn đó.",
            "Hãy dành ít phút cho mục tiêu hôm nay nhé!",
            "Mỗi phút hôm nay đều có ý nghĩa. Bắt đầu thôi!",
            "Bạn đã đi xa lắm rồi. Giữ nhịp nào!",
    };

    public static int indexForToday() {
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return dayOfYear % TITLES.length;
    }

    public static String getTitle() {
        return TITLES[indexForToday()];
    }

    public static String getBody() {
        return BODIES[indexForToday()];
    }
}
