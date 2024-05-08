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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    private TextView tvNetworkSpeed;
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
                // tvSpeed.setText("Time: " + getCurrentTime());
                // function to execute
                monitorNetworkSpeed();
                handler.postDelayed(this, 500); // update every second
            }
        }, 500);
        // tvNetworkSpeed = findViewById(R.id.tvSpeed);
        // lastTotalRxBytes = getTotalRxBytes();
        // lastTimeStamp = System.currentTimeMillis();
        // monitorSpeed();
        // monitorNetworkSpeed();
    }

    private void monitorNetworkSpeed() {
        new Thread(() -> {
            while (true) {
                // Simulate network speed reading
                Long[] speed = getNetworkSpeed();
                Long DOWNLOAD_toBits = speed[0] * 8;
                Long DOWNLOAD_toKilobits = DOWNLOAD_toBits / 1024;
                String DOWNLOAD = DOWNLOAD_toKilobits.toString();
                Long UPLOAD_toBits = speed[1] * 8;
                Long UPLOAD_toKilobits = UPLOAD_toBits / 1024;
                String UPLOAD = UPLOAD_toKilobits.toString();
                String SPEED = TrafficsNetworks.getNetworkSpeed();
                runOnUiThread(() -> tvSpeed.setText(("SPEED: "+ SPEED)));
                // runOnUiThread(() -> tvSpeed.setText("Network Speed: " + speed[0].toString()));
                try {
                    Thread.sleep(300); // Update interval
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Long[] getNetworkSpeed() {
        // Implement network speed calculation here
        Long download = TrafficStats.getTotalRxBytes();
        Long upload = TrafficStats.getTotalTxBytes();
        Long[] return_value = {download, upload};
        return return_value;
    }
}