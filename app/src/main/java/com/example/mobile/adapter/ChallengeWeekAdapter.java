package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.ChallengeWeek;

import java.util.List;

public class ChallengeWeekAdapter extends RecyclerView.Adapter<ChallengeWeekAdapter.ChallengeWeekViewHolder> {
    private final List<ChallengeWeek> challengeWeekList;

    public ChallengeWeekAdapter(List<ChallengeWeek> challengeWeekList) {
        this.challengeWeekList = challengeWeekList;
    }

    @NonNull
    @Override
    public ChallengeWeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_week, parent, false);
        return new ChallengeWeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeWeekViewHolder holder, int position) {
        ChallengeWeek day = challengeWeekList.get(position);
        holder.shortName.setText(day.getShortName());
        switch (day.getStatus()) {
            case CHECKED:
                holder.checkStatus.setImageResource(R.drawable.ic_challenge_week_checked);
                break;
            case UNCHECKED:
                holder.checkStatus.setImageResource(R.drawable.ic_challenge_week_unchecked);
                break;
            case MISSED:
                holder.checkStatus.setImageResource(R.drawable.ic_challenge_week_misscheck);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return challengeWeekList.size();
    }

    static class ChallengeWeekViewHolder extends RecyclerView.ViewHolder {
        TextView shortName;
        ImageView checkStatus;

        public ChallengeWeekViewHolder(@NonNull View itemView) {
            super(itemView);
            shortName = itemView.findViewById(R.id.shortName);
            checkStatus = itemView.findViewById(R.id.checkStatus);
        }
    }
}
