package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Accent;

import java.util.List;

public class AccentAdapter extends RecyclerView.Adapter<AccentAdapter.AccentViewHolder> {
    private List<Accent> accentList;

    public AccentAdapter(List<Accent> accentList) {
        this.accentList = accentList;
    }

    @NonNull
    @Override
    public AccentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accent_card, parent, false);
        return new AccentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccentViewHolder holder, int position) {
        Accent accent = accentList.get(position);
        holder.accentName.setText(accent.getName());
        holder.accentCountry.setText(accent.getCountry());

        // Tuỳ ý gắn thêm listener nếu cần
        // holder.iconVolume.setOnClickListener(...)
    }

    @Override
    public int getItemCount() {
        return accentList.size();
    }

    public static class AccentViewHolder extends RecyclerView.ViewHolder {
        TextView accentName, accentCountry;
        ImageView iconVolume, iconChevron;

        public AccentViewHolder(@NonNull View itemView) {
            super(itemView);
            accentName = itemView.findViewById(R.id.textViewAccentName);
            accentCountry = itemView.findViewById(R.id.textViewAccentCountry);
            iconVolume = itemView.findViewById(R.id.iconVolume);
            iconChevron = itemView.findViewById(R.id.iconChevron);
        }
    }
}
