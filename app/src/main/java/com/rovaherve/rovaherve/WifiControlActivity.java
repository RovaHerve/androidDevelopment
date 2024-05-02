package com.rovaherve.rovaherve;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;

public class WifiControlActivity extends AppCompatActivity {

    private static final String TAG = "WIFICONTROL";

    // WIFI管理器
    Activity activity = this;
    // activity = this;
    private ActivityResultLauncher<Intent> wifiPanelLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_control);

        Button enableWifiButton = findViewById(R.id.enable_wifi_button);
        Button disableWifiButton = findViewById(R.id.disable_wifi_button);

        String phoneNumber = "1234567890";

        // Create an Intent with the ACTION_CALL action
        Intent intent = new Intent(Intent.ACTION_REBOOT);

        // Set the data (phone number) for the Intent
        // intent.setData(Uri.parse("tel:" + phoneNumber));

        // Check if the app has permission to make calls
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.REBOOT) != PackageManager.PERMISSION_GRANTED) {
//            // Request the permission if not granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REBOOT}, 1);
//        } else {
//            // Start the activity to initiate the call
//            startActivity(intent);
//        }

        wifiPanelLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Handle the result here, if needed
                });

        enableWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the app has permission to make calls
                if (ContextCompat.checkSelfPermission(WifiControlActivity.this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission if not granted
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 1);
                } else {
                    // Start the activity to initiate the call
                    // startActivity(intent);
                    enableWifi();
                }
                // WifiUtils.enableWifi(WifiControlActivity.this);
                // switchWiFi(true);
                Log.d(TAG, "LOG WIFICONTROL");
            }
        });

        disableWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WifiUtils.disableWifi(WifiControlActivity.this);
                // switchWiFi(false);
                Log.d(TAG, "LOG WIFICONTROL");
                // Phone number to call
                String phoneNumber = "1234567890";

                // Create an Intent with the ACTION_CALL action
                Intent intent = new Intent(Intent.ACTION_CALL);

                // Set the data (phone number) for the Intent
                intent.setData(Uri.parse("tel:" + phoneNumber));

                // Check if the app has permission to make calls
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // Request the permission if not granted
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
//                } else {
//                    // Start the activity to initiate the call
//                    startActivity(intent);
//                }
            }
        });


    }

    protected void switchWiFi(boolean isOn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
            wifiPanelLauncher.launch(panelIntent);
        } else {
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiMgr != null) {
                wifiMgr.setWifiEnabled(isOn);
            }
        }
    }

    public static void enableWifi() {
        try {
            // Create a ProcessBuilder for adb command to enable Wi-Fi
            ProcessBuilder processBuilder = new ProcessBuilder("adb", "shell", "svc", "wifi", "enable");

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
