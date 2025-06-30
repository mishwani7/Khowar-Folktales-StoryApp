package com.khowarfolktales.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    private static final String CHANNEL_ID = "DownloadChannel";
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Download Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String videoUrl = intent.getStringExtra("videoUrl");
        String title = intent.getStringExtra("title");
        startForeground(notificationId, createNotification("Starting download..."));

        new Thread(() -> {
            downloadVideo(videoUrl, title);
            stopSelf();
        }).start();

        return START_STICKY;
    }

    private void downloadVideo(String videoUrl, String title) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Sanitize title for filename
            String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
            File videoFile = new File(getCacheDir(), sanitizedTitle + ".mp4");

            URL url = new URL(videoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Check for successful response code
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("DownloadService", "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                return;
            }

            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(videoFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            long fileLength = connection.getContentLength();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Update notification with progress
                int progress = (int) (totalBytesRead * 100 / fileLength);
                notificationBuilder.setProgress(100, progress, false);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }

            // Download complete
            notificationBuilder.setContentText("Download complete")
                    .setProgress(0, 0, false);
            notificationManager.notify(notificationId, notificationBuilder.build());

        } catch (Exception e) {
            Log.e("DownloadService", "Error downloading video: " + e.toString());
            notificationBuilder.setContentText("Download failed")
                    .setProgress(0, 0, false);
            notificationManager.notify(notificationId, notificationBuilder.build());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                Log.e("DownloadService", "Error closing streams: " + e.toString());
            }
        }
    }

    private Notification createNotification(String contentText) {
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Downloading Video")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_download) // Replace with your download icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);
        return notificationBuilder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}