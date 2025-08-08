package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Dialect;

import java.util.List;

public class AccentAdapter extends RecyclerView.Adapter<AccentAdapter.ViewHolder> {
    private List<Dialect> dialectList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Dialect dialect);  // 👈 Đảm bảo là Dialect
    }

    public AccentAdapter(List<Dialect> list, OnItemClickListener listener) {
        this.dialectList = list;
        this.listener = listener;
    }

    public void setData(List<Dialect> data) {
        this.dialectList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccentAdapter.ViewHolder holder, int position) {
        Dialect dialect = dialectList.get(position);

        String region = dialect.getRegion();
        holder.accentTitle.setText("English (" + region + ")");
        holder.accentRegion.setText(region);
        holder.flag.setText(getFlagEmoji(region.toLowerCase()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(dialect);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dialectList != null ? dialectList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accentTitle, accentRegion, flag;

        public ViewHolder(View itemView) {
            super(itemView);
            accentTitle = itemView.findViewById(R.id.textAccentTitle);
            accentRegion = itemView.findViewById(R.id.textAccentRegion);
            flag = itemView.findViewById(R.id.textFlag);
        }
    }
    private String getFlagEmoji(String region) {
        switch (region) {
            case "philippines": return "🇵🇭";
            case "india": return "🇮🇳";
            case "kenya": return "🇰🇪";
            case "tanzania": return "🇹🇿";
            case "south africa": return "🇿🇦";
            case "singapore": return "🇸🇬";
            case "hong kong": return "🇭🇰";
            case "new zealand": return "🇳🇿";
            case "uk": return "🇬🇧";
            case "us":
            case "usa":
            case "united states": return "🇺🇸";
            default: return "🌍";
        }
    }
}
