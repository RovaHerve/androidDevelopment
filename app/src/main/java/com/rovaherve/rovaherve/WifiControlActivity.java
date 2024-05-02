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

public class WifiControlActivity extends AppCompatActivity {

    private static final String TAG = "WIFICONTROL";
    Activity activity;
    // activity = this;
    private ActivityResultLauncher<Intent> wifiPanelLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_control);

        Button enableWifiButton = findViewById(R.id.enable_wifi_button);
        Button disableWifiButton = findViewById(R.id.disable_wifi_button);

        wifiPanelLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Handle the result here, if needed
                });

        enableWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WifiUtils.enableWifi(WifiControlActivity.this);
                switchWiFi(true);
                Log.d(TAG, "LOG WIFICONTROL");
            }
        });

        disableWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WifiUtils.disableWifi(WifiControlActivity.this);
                // switchWiFi(false);
                Log.d(TAG, "LOG WIFICONTROL");
                // int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
                // if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WifiControlActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 225);
                // } else {
                    //TODO
                // }

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+261384722127"));
                startActivity(intent);
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
}
