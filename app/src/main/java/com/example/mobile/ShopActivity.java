package com.example.mobile; // Đảm bảo package này khớp với package của dự án bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout; // Để bắt sự kiện click cho các item
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop); // Đảm bảo file layout là activity_shop.xml

        // --- Back Button Setup ---
        ImageView btnBack = findViewById(R.id.btn_back_shop);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed()); // Quay lại Activity trước đó
        }

        // --- Update Diamonds Count (Example) ---
        TextView tvDiamondsCount = findViewById(R.id.tv_diamonds_count_shop);
        if (tvDiamondsCount != null) {
            // tvDiamondsCount.setText("250"); // Bạn có thể cập nhật số kim cương thực tế ở đây
        }

        // --- Boosters & Power-ups Click Listeners ---

        // Heart Shield Item
        LinearLayout itemHeartShield = findViewById(R.id.item_heart_shield);
        if (itemHeartShield != null) {
            itemHeartShield.setOnClickListener(v -> Toast.makeText(ShopActivity.this, "Heart Shield item clicked!", Toast.LENGTH_SHORT).show());
        }
        LinearLayout btnRedeemHeartShield = findViewById(R.id.btn_redeem_heart_shield);
        if (btnRedeemHeartShield != null) {
            btnRedeemHeartShield.setOnClickListener(v -> Toast.makeText(ShopActivity.this, "Redeem Heart Shield button clicked!", Toast.LENGTH_SHORT).show());
        }

        // Double Diamonds Item
        LinearLayout itemDoubleDiamonds = findViewById(R.id.item_double_diamonds);
        if (itemDoubleDiamonds != null) {
            itemDoubleDiamonds.setOnClickListener(v -> Toast.makeText(ShopActivity.this, "Double Diamonds item clicked!", Toast.LENGTH_SHORT).show());
        }
        LinearLayout btnRedeemDoubleDiamonds = findViewById(R.id.btn_redeem_double_diamonds);
        if (btnRedeemDoubleDiamonds != null) {
            btnRedeemDoubleDiamonds.setOnClickListener(v -> Toast.makeText(ShopActivity.this, "Redeem Double Diamonds button clicked!", Toast.LENGTH_SHORT).show());
        }

        // --- Exchange Click Listeners ---

        // 20 Diamonds for 1 Heart Item
        LinearLayout itemExchangeHeart = findViewById(R.id.item_exchange_heart);
        if (itemExchangeHeart != null) {
            itemExchangeHeart.setOnClickListener(v -> Toast.makeText(ShopActivity.this, "Exchange Heart item clicked!", Toast.LENGTH_SHORT).show());
        }
        LinearLayout btnRedeemExchangeHeart = findViewById(R.id.btn_redeem_exchange_heart);
        if (btnRedeemExchangeHeart != null) {
            btnRedeemExchangeHeart.setOnClickListener(v -> Toast.makeText(ShopActivity.this, "Redeem Exchange Heart button clicked!", Toast.LENGTH_SHORT).show());
        }
    }
}