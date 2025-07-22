// ✅ SampleAdapter.java sửa đúng

package com.example.mobile.adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R; // ✅ Đảm bảo import đúng R
import com.example.mobile.model.SpeakingDialect;

import java.io.IOException;
import java.util.List;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {
    private List<SpeakingDialect> items;
    private MediaPlayer mediaPlayer;

    public SampleAdapter(List<SpeakingDialect> items) {
        this.items = items;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SpeakingDialect item = items.get(position);
        holder.textOption.setText(item.getPrompt());

        holder.btnPlay.setOnClickListener(v -> {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                mediaPlayer.setDataSource(item.getAudioUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Toast.makeText(holder.itemView.getContext(), "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textOption;
        ImageButton btnPlay;

        ViewHolder(View itemView) {
            super(itemView);
            textOption = itemView.findViewById(R.id.textOption); // ✅ sửa đúng id
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }
}
