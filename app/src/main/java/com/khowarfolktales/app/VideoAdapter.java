package com.khowarfolktales.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<Story> videoList;
    private Context context;

    public VideoAdapter(List<Story> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each video item
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watch_later_video, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Story video = videoList.get(position);
        // Bind the video data to the views in the item layout
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private Button playButton;
        private TextView titleTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            playButton = itemView.findViewById(R.id.playButton);
            titleTextView = itemView.findViewById(R.id.title);
        }

        public void bind(Story video) {
            // Set the video title in the TextView
            titleTextView.setText(video.getTitle());

            // Set the video thumbnail using Glide
            String videoId = extractYouTubeVideoId(video.getYoutubeLink());
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
            Glide.with(itemView.getContext())
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(thumbnailImageView);

            // Set up the play button click listener
            playButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("title", video.getTitle());
                intent.putExtra("youtubeLink", video.getYoutubeLink());
                intent.putExtra("urduText", video.getUrduText());
                intent.putExtra("khowarText", video.getKhowarText());
                intent.putExtra("videoDownloadLink", video.getVideoDownloadLink());
                context.startActivity(intent);
            });
        }

        // Helper method to extract the YouTube video ID from a URL
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
}