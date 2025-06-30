package com.khowarfolktales.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 100;
    private YouTubePlayerView youTubePlayerView;
    private TextView storyTextView, watchLaterTextView, downloadTextView;
    private String videoId, videoLink, urduText, khowarText, videoDownloadLink;
    private boolean isUrduSelected = true;
    private ProgressBar progressBar;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        }

        // Initialize views
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        storyTextView = findViewById(R.id.storyTextView);
        watchLaterTextView = findViewById(R.id.watchLaterTextView);
        downloadTextView = findViewById(R.id.downloadTextView);
        progressBar = findViewById(R.id.progressBar);

        // Get the intent data
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        videoLink = intent.getStringExtra("youtubeLink");
        urduText = intent.getStringExtra("urduText");
        khowarText = intent.getStringExtra("khowarText");
        videoDownloadLink = intent.getStringExtra("videoDownloadLink"); // Firebase download link

        videoId = extractYouTubeVideoId(videoLink);

        // Set the toolbar title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(getResources().getColor(android.R.color.white));

        // Load the YouTube video
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        // Set initial story text
        storyTextView.setText(urduText);

        // Language toggle
        findViewById(R.id.toggleLanguageButton).setOnClickListener(v -> {
            if (isUrduSelected) {
                storyTextView.setText(khowarText);
                ((TextView) v).setText("Switch to Urdu");
            } else {
                storyTextView.setText(urduText);
                ((TextView) v).setText("Switch to Khowar");
            }
            isUrduSelected = !isUrduSelected;
        });

        // Add to Watch Later
        watchLaterTextView.setOnClickListener(v -> {
            // Ensure that all the required fields are passed correctly to the Story constructor
            WatchLaterActivity.watchedVideos.add(new Story(title, "", videoLink, 0, urduText, khowarText, videoDownloadLink));
            Toast.makeText(DetailsActivity.this, "Added to Watch Later", Toast.LENGTH_SHORT).show();
        });

        // Trigger download using Firebase download link
        downloadTextView.setOnClickListener(v -> {
            if (videoDownloadLink != null && !videoDownloadLink.isEmpty()) {
                requestNotificationPermission(title);
            } else {
                Toast.makeText(DetailsActivity.this, "No download link available", Toast.LENGTH_SHORT).show();
            }
        });

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_stories) {
                startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_watch_later) {
                startActivity(new Intent(DetailsActivity.this, WatchLaterActivity.class));
                return true;
            } else if (item.getItemId() == R.id.download_video) {
                startActivity(new Intent(DetailsActivity.this, DownloadActivity.class));
                return true;
            }
            return false;
        });

        // Register the NetworkChangeReceiver
        networkChangeReceiver = new NetworkChangeReceiver(this);
        registerNetworkReceiver();
    }

    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void unregisterNetworkReceiver() {
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
        unregisterNetworkReceiver(); // Unregister the receiver
    }

    // Extract YouTube video ID from link
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNotificationPermission(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
            } else {
                startDownloadService(title);
            }
        } else {
            startDownloadService(title);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloadService(getIntent().getStringExtra("title"));
            } else {
                Toast.makeText(this, "Notification permission is required to show download progress.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDownloadService(String title) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("videoUrl", videoDownloadLink);
        intent.putExtra("title", title);
        ContextCompat.startForegroundService(this, intent);
    }

    // AsyncTask class for downloading video from Firebase link
    public class DownloadVideoTask extends AsyncTask<String, Integer, File> {
        private Context context;
        private ProgressBar progressBar;
        private String title; // Added to use title for the filename

        public DownloadVideoTask(Context context, ProgressBar progressBar, String title) {
            this.context = context;
            this.progressBar = progressBar;
            this.title = title; // Pass the title to the constructor
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected File doInBackground(String... params) {
            String videoUrl = params[0];
            File videoFile = null;

            try {
                // Sanitize title for filename and use it instead of a random name
                String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
                String videoFileName = sanitizedTitle + ".mp4"; // Use title for the filename

                File cacheDir = context.getCacheDir();
                videoFile = new File(cacheDir, videoFileName); // File saved in cache directory

                URL url = new URL(videoUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(videoFile);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                Log.e("ErrorDownload", "doInBackground: " + e.toString());
                e.printStackTrace();
                return null;
            }

            return videoFile;
        }

        @Override
        protected void onPostExecute(File result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                Toast.makeText(context, "Downloaded to: " + result.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}