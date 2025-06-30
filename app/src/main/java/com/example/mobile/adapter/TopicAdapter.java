package com.example.mobile.adapter;
import com.example.mobile.R;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.model.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    private List<Topic> topicList;

    public TopicAdapter(List<Topic> topicList) {
        this.topicList = topicList;
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ProgressBar progressBar;
        TextView statusText;
        TextView totalText;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.speakingTitle);
            progressBar = itemView.findViewById(R.id.progressBarGreen);
            statusText = itemView.findViewById(R.id.progressTextSpeaking);
            totalText = itemView.findViewById(R.id.totalOfTopic);
        }
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.learning_card_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.titleText.setText(topic.getTitle());
        holder.progressBar.setProgress(topic.getProgress());
        holder.statusText.setText(topic.getStatusText());
        holder.totalText.setText(topic.getTotal() + " lessons");

        // Màu theo trạng thái
        if ("Completed".equalsIgnoreCase(topic.getStatusText())) {
            holder.statusText.setTextColor(Color.parseColor("#F57C00")); //cam
        } else {
            holder.statusText.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}
