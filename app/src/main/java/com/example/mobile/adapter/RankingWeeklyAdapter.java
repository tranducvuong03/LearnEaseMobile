package com.example.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobile.R;
import com.example.mobile.model.RankingItem;

import java.util.ArrayList;
import java.util.List;

public class RankingWeeklyAdapter extends RecyclerView.Adapter<RankingWeeklyAdapter.ViewHolder> {
    private List<RankingItem> rankingList = new ArrayList<>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankWeekly, tvNameWeekly, tvPointsWeekly;
        ImageView imgAvatarWeekly;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvRankWeekly = itemView.findViewById(R.id.tvRankWeekly);
            tvNameWeekly = itemView.findViewById(R.id.tvNameWeekly);
            tvPointsWeekly = itemView.findViewById(R.id.tvPointsWeekly);
            imgAvatarWeekly = itemView.findViewById(R.id.imgAvatarWeekly);
        }
    }
    public RankingWeeklyAdapter(Context context, List<RankingItem> rankingList) {
        this.context = context;
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingWeeklyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.example.mobile.R.layout.item_ranking_weekly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingWeeklyAdapter.ViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);

        holder.tvRankWeekly.setText(String.valueOf(position + 4)); // vì top 3 đã hiển thị riêng
        holder.tvNameWeekly.setText(item.getName());
        holder.tvPointsWeekly.setText(item.getScore() + " points");

        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.user1)
                .circleCrop()
                .into(holder.imgAvatarWeekly);

    }

    @Override
    public int getItemCount() {
        return rankingList != null ? rankingList.size() : 0;
    }
    public void setData(List<RankingItem> data) {
        this.rankingList = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

}
