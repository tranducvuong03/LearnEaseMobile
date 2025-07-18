package com.example.mobile.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.mobile.TopicLessonsActivity;
import com.example.mobile.model.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private List<Topic> topicList;
    private Context context; // Thêm context

    public TopicAdapter(Context context, List<Topic> topicList) {
        this.context = context;  // Lưu lại context
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

        // Thay đổi màu sắc tùy theo trạng thái
        if ("Completed".equalsIgnoreCase(topic.getStatus())) {
            holder.statusText.setTextColor(Color.parseColor("#F57C00"));
        } else {
            holder.statusText.setTextColor(Color.GRAY);
        }

        // Bắt sự kiện click vào mỗi item
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent chuyển đến ActivityTopicLessons
            Intent intent = new Intent(context, TopicLessonsActivity.class);
            intent.putExtra("topic_id", topic.getTopicId());  // Truyền thông tin về topic
            intent.putExtra("topic_name", topic.getTitle());  // Truyền tên của topic
            context.startActivity(intent);  // Mở ActivityTopicLessons
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}
