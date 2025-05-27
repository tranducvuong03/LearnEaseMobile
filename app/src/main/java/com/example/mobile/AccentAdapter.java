package com.example.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AccentAdapter extends RecyclerView.Adapter<AccentAdapter.AccentViewHolder> {
    private final List<Accent> accents;
    public AccentAdapter(List<Accent> accents) {
        this.accents = accents;
    }
    @NonNull
    @Override
    public AccentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accent_card, parent, false);
        return new AccentViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AccentViewHolder holder, int position) {
        Accent accent = accents.get(position);
        holder.name.setText(accent.name);
        holder.country.setText(accent.country);
    }
    @Override
    public int getItemCount() {
        return accents.size();
    }
    static class AccentViewHolder extends RecyclerView.ViewHolder {
        TextView name, country;
        ImageView iconVolume, iconChevron;
        AccentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewAccentName);
            country = itemView.findViewById(R.id.textViewAccentCountry);
            iconVolume = itemView.findViewById(R.id.iconVolume);
            iconChevron = itemView.findViewById(R.id.iconChevron);
        }
    }
} 