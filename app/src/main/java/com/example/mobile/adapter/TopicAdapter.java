package com.example.mobile.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private List<Topic> topicList;

    public TopicAdapter(List<Topic> topicList) {
        this.topicList = topicList;
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, statusText, totalText;
        ProgressBar progressBar;
        ImageView iconChevron;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.topicTitle);
            statusText = itemView.findViewById(R.id.progressTextSpeaking);
            totalText = itemView.findViewById(R.id.totalOfTopic);
            progressBar = itemView.findViewById(R.id.progressBarGreen);
            iconChevron = itemView.findViewById(R.id.iconChevron);
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
        holder.totalText.setText(topic.getCompletedLessons() + "/" + topic.getTotalLessons() + " lessons");
        holder.statusText.setText(topic.getStatus());
        holder.progressBar.setProgress(topic.getProgressPercent());

        if ("Completed".equalsIgnoreCase(topic.getStatus())) {
            holder.statusText.setTextColor(Color.parseColor("#F57C00"));
        } else {
            holder.statusText.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}
