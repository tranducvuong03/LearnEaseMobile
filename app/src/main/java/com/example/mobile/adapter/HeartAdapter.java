package com.example.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mobile.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeartAdapter extends RecyclerView.Adapter<HeartAdapter.HeartViewHolder>{
    private int heartCount;
    private Context context;

    public HeartAdapter(Context context, int heartCount) {
        this.context = context;
        this.heartCount = heartCount;
    }

    public static class HeartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHeart;

        public HeartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHeart = itemView.findViewById(R.id.img_heart);
        }
    }

    @NonNull
    @Override
    public HeartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_heart_shop, parent, false);
        return new HeartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeartViewHolder holder, int position) {
        if (position < heartCount) {
            holder.imgHeart.setImageResource(R.drawable.active_heart_image);
        } else {
            holder.imgHeart.setImageResource(R.drawable.inactive_heart_image);
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Luôn hiển thị 5 tim
    }

    public void updateHeartCount(int count) {
        heartCount = Math.max(0, Math.min(count, 5)); // giới hạn từ 0 đến 5
        notifyDataSetChanged();
    }
    public static String formatMinutesToTimeText(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
}
