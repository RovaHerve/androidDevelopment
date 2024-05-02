package com.rovaherve.rovaherve;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiUtils {
    // Method to turn on the Wi-Fi
    public static void enableWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // Method to turn off the Wi-Fi
    public static void disableWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }
}
