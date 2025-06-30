package com.khowarfolktales.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private List<Story> filteredStoryList;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ProgressDialog progressDialog;
    private Button goToDownloadsButton;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.storyRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize lists and adapter
        storyList = new ArrayList<>();
        filteredStoryList = new ArrayList<>();
        storyAdapter = new StoryAdapter(this, filteredStoryList);
        recyclerView.setAdapter(storyAdapter);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Initialize the Go to Downloads button
        goToDownloadsButton = findViewById(R.id.goToDownloadsButton);

        // Set OnClickListener for the Go to Downloads button
        goToDownloadsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivity(intent);
        });

        // Register the NetworkChangeReceiver
        networkChangeReceiver = new NetworkChangeReceiver(this);
        registerNetworkReceiver();

        // Fetch data from Firebase
        fetchDataFromFirebase();

        // Initialize the ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevents dismissing the dialog by clicking outside

        // Set up Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_stories) {
                // Already on Stories
                return true;
            } else if (item.getItemId() == R.id.nav_watch_later) {
                // Show the loading dialog
                progressDialog.show();

                // Launch Watch Later Activity
                Intent intent = new Intent(MainActivity.this, WatchLaterActivity.class);

                // Start the WatchLaterActivity with a delay to simulate loading
                new Handler().postDelayed(() -> {
                    progressDialog.dismiss(); // Dismiss the loader
                    startActivity(intent);
                }, 1000); // Adjust the delay as needed

                return true;
            } else if (item.getItemId() == R.id.download_video) {
                // Show the loading dialog
                progressDialog.show();

                // Launch Download Activity
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);

                // Start the DownloadActivity with a delay to simulate loading
                new Handler().postDelayed(() -> {
                    progressDialog.dismiss(); // Dismiss the loader
                    startActivity(intent);
                }, 1000); // Adjust the delay as needed

                return true;
            }
            return false;
        });

        // Set the active menu item
        bottomNavigationView.setSelectedItemId(R.id.nav_stories);

        // Handle story item clicks to open DetailsActivity
        storyAdapter.setOnItemClickListener(story -> openDetailsActivity(story));

        // Check initial network status
        checkNetworkStatus();
    }

    private void fetchDataFromFirebase() {
        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Story story = postSnapshot.getValue(Story.class);
                    if (story != null && story.getTitle() != null) {
                        // Increment the views count
                        story.setViews(story.getViews() + 1);
                        storyList.add(story);
                    }
                }
                filteredStoryList.clear();
                filteredStoryList.addAll(storyList);
                storyAdapter.notifyDataSetChanged();

                // Update the view count in Firebase
                updateViewCountInFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void updateViewCountInFirebase() {
        for (Story story : storyList) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("views", story.getViews());
            databaseReference.child(story.getTitle()).updateChildren(updates);
        }
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

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the NetworkChangeReceiver
        unregisterNetworkReceiver();
    }

    private void openDetailsActivity(Story story) {
        // Show the loading dialog
        progressDialog.show();

        // Intent to open DetailsActivity
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("title", story.getTitle());
        intent.putExtra("youtubeLink", story.getYoutubeLink());
        intent.putExtra("urduText", story.getUrduText());
        intent.putExtra("khowarText", story.getKhowarText());
        intent.putExtra("videoDownloadLink", story.getVideoDownloadLink()); // Added videoDownloadLink

        // Start the DetailsActivity with a delay to simulate loading
        new Handler().postDelayed(() -> {
            progressDialog.dismiss(); // Dismiss the loader
            startActivity(intent);
        }, 1000); // Adjust the delay as needed
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Search functionality
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint("Search stories...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterStories(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterStories(newText);
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_settings) {
            // Handle settings click
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterStories(String query) {
        filteredStoryList.clear();
        if (query.isEmpty()) {
            filteredStoryList.addAll(storyList);
        } else {
            for (Story story : storyList) {
                if (story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredStoryList.add(story);
                }
            }
        }
        storyAdapter.notifyDataSetChanged();
    }

    // Method to check network status and update button visibility
    public void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Update button visibility
        if (isConnected) {
            goToDownloadsButton.setVisibility(View.GONE);
        } else {
            goToDownloadsButton.setVisibility(View.VISIBLE);
        }
    }
}