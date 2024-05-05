package com.rovaherve.rovaherve;

import android.Manifest;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    // private long lastTotalRxBytes = 0;
    // private long lastTimeStamp = 0;
    // private TextView tvNetworkSpeed;
    private TextView tvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSpeed = findViewById(R.id.tvSpeed);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvSpeed.setText("Time: " + getCurrentTime());
                handler.postDelayed(this, 1000); // update every second
            }
        }, 1000);
        // monitor network speed
        // tvNetworkSpeed = findViewById(R.id.tvSpeed);
        // lastTotalRxBytes = getTotalRxBytes();
        // lastTimeStamp = System.currentTimeMillis();
        // monitorSpeed();
        // Start monitoring network speed
        // monitorNetworkSpeed();
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}

//    private void monitorNetworkSpeed() {
//        // Dummy example to continuously update network speed
//        new Thread(() -> {
//            while (true) {
//                // Simulate network speed reading
//                final Long speed = getNetworkSpeed();
//                runOnUiThread(() -> tvSpeed.setText("Network Speed: " + speed));
//
//                try {
//                    Thread.sleep(1000); // Update interval
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    private Long getNetworkSpeed() {
//        // Implement network speed calculation here
//        // return getTotalRxBytes(); // Dummy speed
//        return TrafficStats.getTotalRxBytes();
//    }

//    private void monitorSpeed() {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                long currentRxBytes = TrafficStats.getTotalRxBytes();
//                long currentTimeStamp = System.currentTimeMillis();
//                long usedData = currentRxBytes - lastTotalRxBytes;
//                long duration = currentTimeStamp - lastTimeStamp;
//                double speed = ((usedData * 1000) / duration) * 8; // speed in bits per second
//                double speedMbps = speed / (1024);
//                tvNetworkSpeed.setText(String.format("%.2f Mbps", speedMbps));
//                lastTotalRxBytes = currentRxBytes;
//                lastTimeStamp = currentTimeStamp;
//                handler.postDelayed(this, 1000); // update every second
//            }
//        }, 1000);
//    }

//    private long getTotalRxBytes() {
//        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
//    }
//}
