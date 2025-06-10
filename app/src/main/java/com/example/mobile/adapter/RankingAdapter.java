package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.RankingItem;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private List<RankingItem> rankingList;

    public RankingAdapter(List<RankingItem> rankingList) {
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ranking, parent, false);

        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPoints.setText(item.getPoints() + " points");
        holder.tvRank.setText("#" + item.getRank());
        holder.imgAvatar.setImageResource(item.getAvatarResId());
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public void updateData(List<RankingItem> newList) {
        this.rankingList = newList;
        notifyDataSetChanged();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPoints, tvRank;
        ImageView imgAvatar;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_dashboard_title);
            tvPoints = itemView.findViewById(R.id.tv_dashboard_title);
            tvRank = itemView.findViewById(R.id.tv_dashboard_title);
            imgAvatar = itemView.findViewById(R.id.menu_explore);
        }
    }
}