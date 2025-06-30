package com.khowarfolktales.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.List;

public class DownloadedVideoAdapter extends ArrayAdapter<File> {

    private Context context;
    private List<File> videoFiles;

    public DownloadedVideoAdapter(@NonNull Context context, @NonNull List<File> videoFiles) {
        super(context, R.layout.item_video, videoFiles);
        this.context = context;
        this.videoFiles = videoFiles;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        }

        // Get the file for this position
        File videoFile = videoFiles.get(position);

        // Get views from the layout
        ImageView thumbnailImageView = convertView.findViewById(R.id.thumbnailImageView);
        TextView titleTextView = convertView.findViewById(R.id.title);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button playButton = convertView.findViewById(R.id.playButton);

        // Extract title from the filename (removing the .mp4 extension)
        String fileName = videoFile.getName();
        String title = fileName.substring(0, fileName.lastIndexOf('.'));

        // Set the title in the TextView
        titleTextView.setText(title);

        // Load video thumbnail using Glide
        Glide.with(context)
                .load(videoFile)
                .placeholder(R.drawable.placeholder)  // Placeholder image while loading
                .into(thumbnailImageView);

        // Set up play button click listener
        playButton.setOnClickListener(v -> {
            Uri videoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", videoFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(videoUri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        });

        // Set up delete button click listener
        deleteButton.setOnClickListener(v -> {
            if (videoFile.delete()) {
                videoFiles.remove(position);  // Remove from list
                notifyDataSetChanged();  // Notify adapter about data change
                Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error deleting video", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
