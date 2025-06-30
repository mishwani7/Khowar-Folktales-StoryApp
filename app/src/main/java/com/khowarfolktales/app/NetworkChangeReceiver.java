package com.khowarfolktales.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private Context context;
    private boolean wasConnected = true; // Assume the device is connected initially

    public NetworkChangeReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = isInternetAvailable(context);
        if (isConnected && !wasConnected) {
            showToast("Internet connection restored");
        } else if (!isConnected) {
            showToast("No internet connection");
        }
        wasConnected = isConnected;
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}