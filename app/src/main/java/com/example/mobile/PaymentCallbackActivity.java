package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PaymentCallbackActivity  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent() != null ? getIntent().getData() : null;
        // Forward sang SubscriptionActivity mà KHÔNG reset UI nếu đang ở top
        Intent toSub = new Intent(this, SubscriptionActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (uri != null) toSub.setData(uri);
        startActivity(toSub);

        finish(); // đóng ngay, user không thấy màn hình này
    }
}
