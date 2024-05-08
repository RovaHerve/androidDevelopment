package com.rovaherve.rovaherve.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;
import android.net.TrafficStats;
import android.widget.Toast;

import com.rovaherve.rovaherve.R;

 public class MainActivity extends AppCompatActivity {
    // TrafficNetworks trafficUtils = new TrafficNetworks();

    private TextView uploadSpeedTextView;
    private TextView downloadSpeedTextView;

    private long lastTotalRxBytes = 0;
    private long lastTotalTxBytes = 0;
    private long lastTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadSpeedTextView = findViewById(R.id.uploadSpeedTextView);
        downloadSpeedTextView = findViewById(R.id.downloadSpeedTextView);

        // Start periodic update task
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // updateSpeed();
            }
        }, 0, 1000); // Update speed every second
    }

    private void updateSpeed() {
        String test = TrafficNetworks.getNetworkSpeed();
        long currentRxBytes = TrafficStats.getMobileRxBytes();
        long currentTxBytes = TrafficStats.getMobileTxBytes() ;
        long currentTimeStamp = System.currentTimeMillis();
        long downloadSpeed = calculateSpeed(lastTotalRxBytes, currentRxBytes, lastTimeStamp, currentTimeStamp);
        long uploadSpeed = calculateSpeed(lastTotalTxBytes, currentTxBytes, lastTimeStamp, currentTimeStamp);

        // Update download and upload speed TextViews
        runOnUiThread(() -> {
            downloadSpeedTextView.setText(String.format("Download Speed: %d KB/s", test));
            // uploadSpeedTextView.setText(String.format("Upload Speed: %d KB/s", uploadSpeed));
        });

        lastTotalRxBytes = currentRxBytes;
        lastTotalTxBytes = currentTxBytes;
        lastTimeStamp = currentTimeStamp;
    }

    private long getTotalRxBytes() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // return networkInfo.getTotalRxBytes();
            return TrafficStats.getTotalRxBytes();
        }
        return 0;
    }

    private long getTotalTxBytes() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // return networkInfo.getTotalTxBytes();
            return TrafficStats.getTotalTxBytes();
        }
        return 0;
    }

    private long calculateSpeed(long lastTotalBytes, long currentTotalBytes, long lastTimeStamp, long currentTimeStamp) {
        long bytesDelta = currentTotalBytes - lastTotalBytes;
        long timeDelta = currentTimeStamp - lastTimeStamp;
        if (timeDelta > 0) {
            // return 66;
            return (bytesDelta * 1000) / timeDelta; // Convert to KB/s
        }
        return 0;
    }

//    private String networkSpeed() {
//        return trafficUtils.getNetworkSpeed();
//    }

}
