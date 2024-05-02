package com.rovaherve.rovaherve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
public class NetworkReceiver extends BroadcastReceiver {

    public static String status = null;

    public void onReceive(Context context, Intent intent) {
        status = NetworkUtils.getConnectivityStatusString(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        if(status.equals("WIFI_ENABLED")){
            //your code when wifi enable
            Toast.makeText(context, "WIFI ENABLED", Toast.LENGTH_SHORT).show();
        }
        else if(status.equals("MOBILE_DATA_ENABLED")){
            //your code when TYPE_MOBILE network enable
            Toast.makeText(context, "MOBILE DATA ENABLED", Toast.LENGTH_SHORT).show();
        }
        else if(status.equals("NOT_CONNECTED")){
            //your code when no network connected
            Toast.makeText(context, "NOT CONNECTED", Toast.LENGTH_SHORT).show();
        }
    }
}