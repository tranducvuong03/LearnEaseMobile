package com.example.mobile; // Đảm bảo đúng package của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SubscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đặt layout cho Activity này
        setContentView(R.layout.activity_subscription); // Đảm bảo tên file layout của bạn là activity_subscription.xml

        // SỬA ĐỔI TẠI ĐÂY: Thay đổi R.id.btn_back thành R.id.btn_back_subscription
        ImageView btnBack = findViewById(R.id.btn_back_subscription);

        // Đặt Listener cho nút quay lại
        // Để an toàn hơn, bạn nên kiểm tra null trước khi sử dụng
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Quay lại Activity trước đó trong stack
                    onBackPressed();
                }
            });
        }
        // else {
        //     // Tùy chọn: Thêm log hoặc Toast để cảnh báo nếu không tìm thấy nút
        //     // Log.e("SubscriptionActivity", "btn_back_subscription not found in layout!");
        // }

    }
}