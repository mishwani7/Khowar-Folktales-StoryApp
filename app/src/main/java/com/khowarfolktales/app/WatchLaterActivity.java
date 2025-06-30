package com.khowarfolktales.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class WatchLaterActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView watchedVideosRecyclerView;
    private VideoAdapter videoAdapter;
    private View noContentLayout; // Reference to the no content layout
    public static List<Story> watchedVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_later);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the custom back button drawable
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow); // Set custom back arrow
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide app name
        }

        // Set up the RecyclerView to display watched videos
        watchedVideosRecyclerView = findViewById(R.id.watched_videos_recycler_view);
        watchedVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(watchedVideos, this);
        watchedVideosRecyclerView.setAdapter(videoAdapter);

        // Reference to the no content layout
        noContentLayout = findViewById(R.id.no_content_layout);

        // Check if there are any watched videos
        updateUI();

        // Set up Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_stories) {
                startActivity(new Intent(WatchLaterActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_watch_later) {
                return true; // Already on Watch Later
            } else if (item.getItemId() == R.id.download_video) {
                startActivity(new Intent(WatchLaterActivity.this, DownloadActivity.class));
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_watch_later);

        // Set up Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            watchedVideos.clear();
            videoAdapter.notifyDataSetChanged();
            updateUI(); // Update UI after clearing
        });
    }

    private void updateUI() {
        if (watchedVideos.isEmpty()) {
            watchedVideosRecyclerView.setVisibility(View.GONE);
            noContentLayout.setVisibility(View.VISIBLE); // Show no content message
        } else {
            watchedVideosRecyclerView.setVisibility(View.VISIBLE);
            noContentLayout.setVisibility(View.GONE); // Hide no content message
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle back button press
        return true;
    }
}