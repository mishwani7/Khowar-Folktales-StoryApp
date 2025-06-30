package com.khowarfolktales.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Story story);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public StoryAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.titleTextView.setText(story.getTitle());
        holder.dateTextView.setText(story.getDate());
        holder.viewsTextView.setText(story.getViews() + " views");

        if (story.getYoutubeLink() != null && !story.getYoutubeLink().isEmpty()) {
            String videoId = extractYouTubeVideoId(story.getYoutubeLink());
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(story);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView dateTextView;
        TextView viewsTextView; // New TextView for displaying the view count

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            viewsTextView = itemView.findViewById(R.id.viewsTextView); // Initialize the new viewsTextView
        }
    }

    private String extractYouTubeVideoId(String youtubeLink) {
        String videoId = null;
        if (youtubeLink.contains("v=")) {
            int start = youtubeLink.indexOf("v=") + 2;
            int end = youtubeLink.indexOf("&", start);
            videoId = end == -1 ? youtubeLink.substring(start) : youtubeLink.substring(start, end);
        } else if (youtubeLink.contains("youtu.be/")) {
            int start = youtubeLink.indexOf("youtu.be/") + 9;
            videoId = youtubeLink.substring(start);
        }
        return videoId;
    }
}