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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialect, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccentAdapter.ViewHolder holder, int position) {
        Dialect dialect = dialectList.get(position);
        holder.nameText.setText(dialect.getName() + " (" + dialect.getRegion() + ")");

        // ✅ Đúng: truyền Dialect
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
        TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textDialectName);
        }
    }
}
