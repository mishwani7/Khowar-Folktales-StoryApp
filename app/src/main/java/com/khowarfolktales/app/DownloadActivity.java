package com.khowarfolktales.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView; // Import TextView
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private ListView listView;
    private DownloadedVideoAdapter adapter;
    private List<File> videoFiles;
    private TextView noVideosMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the custom back button drawable
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Downloaded Videos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow); // Set custom back arrow
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white)); // Set title color
        }

        // Initialize ListView and adapter
        listView = findViewById(R.id.downloaded_videos_listview);
        noVideosMessage = findViewById(R.id.no_videos_message);
        videoFiles = new ArrayList<>();
        adapter = new DownloadedVideoAdapter(this, videoFiles);
        listView.setAdapter(adapter);

        // Load the downloaded video files
        loadData();

        // Check if there's a video path passed to this activity
        String videoPath = getIntent().getStringExtra("video_path");
        if (videoPath != null) {
            File videoFile = new File(videoPath);
            playVideo(videoFile);  // Play the video directly if path is passed
        }

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_stories) {
                startActivity(new Intent(DownloadActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_watch_later) {
                startActivity(new Intent(DownloadActivity.this, WatchLaterActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadData() {
        File cacheDir = getCacheDir();
        File[] dataList = cacheDir.listFiles();
        if (dataList != null) {
            videoFiles.clear(); // Clear the list before adding new items
            for (File file : dataList) {
                if (file.getName().endsWith(".mp4")) {  // Filter only video files
                    videoFiles.add(file);
                }
            }
        }

        if (videoFiles.isEmpty()) {
            noVideosMessage.setVisibility(View.VISIBLE); // Show the message
        } else {
            noVideosMessage.setVisibility(View.GONE); // Hide the message
            adapter.notifyDataSetChanged();  // Notify the adapter that data has changed
        }
    }

    private void playVideo(File videoFile) {
        if (videoFile.exists()) {
            Uri videoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", videoFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(videoUri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // Check if there's an app to handle the intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No application available to play video", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteVideo(File videoFile) {
        if (videoFile.delete()) {
            Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();
            loadData();  // Reload the data to reflect the deletion
        } else {
            Toast.makeText(this, "Error deleting video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle back button press
        return true;
    }
}