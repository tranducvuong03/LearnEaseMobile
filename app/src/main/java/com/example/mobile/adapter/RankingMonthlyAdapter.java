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

public class RankingMonthlyAdapter extends RecyclerView.Adapter<RankingMonthlyAdapter.ViewHolder> {
    private Context context;
    private List<RankingItem> rankingList = new ArrayList<>();


    public RankingMonthlyAdapter(Context context, List<RankingItem> rankingList) {
        this.context = context;
        this.rankingList = rankingList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankMonthly, tvNameMonthly, tvPointsMonthly;
        ImageView imgAvatarMonthly, imgCrownMonthly;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRankMonthly = itemView.findViewById(R.id.tvRankMonthly);
            tvNameMonthly = itemView.findViewById(R.id.tvNameMonthly);
            tvPointsMonthly = itemView.findViewById(R.id.tvPointsMonthly);
            imgAvatarMonthly = itemView.findViewById(R.id.imgAvatarMonthly);
            imgCrownMonthly = itemView.findViewById(R.id.imgCrownMonthly);
        }
    }

    @NonNull
    @Override
    public RankingMonthlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking_monthly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingMonthlyAdapter.ViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);

        // Rank
        holder.tvRankMonthly.setText(String.valueOf(position + 1));

        // Name & Score
        holder.tvNameMonthly.setText(item.getName());
        holder.tvPointsMonthly.setText(item.getScore() + " points");

        // Load avatar from URL
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.user1)
                .circleCrop()
                .into(holder.imgAvatarMonthly);

        // Crown theo vị trí
        switch (position) {
            case 0:
                holder.imgCrownMonthly.setVisibility(View.VISIBLE);
                holder.imgCrownMonthly.setImageResource(R.drawable.ranking_top1_crown_1);
                break;
            case 1:
                holder.imgCrownMonthly.setVisibility(View.VISIBLE);
                holder.imgCrownMonthly.setImageResource(R.drawable.ranking_top2_crown);
                break;
            case 2:
                holder.imgCrownMonthly.setVisibility(View.VISIBLE);
                holder.imgCrownMonthly.setImageResource(R.drawable.ranking_top3_crown);
                break;
            default:
                holder.imgCrownMonthly.setVisibility(View.GONE);
                break;
        }


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
