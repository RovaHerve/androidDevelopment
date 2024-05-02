package com.rovaherve.rovaherve;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
public class NetworkUtils {

    static final int TYPE_NOT_CONNECTED = 0;
    static final int TYPE_WIFI = 1;
    static final int TYPE_MOBILE = 2;
    // Method to get mobile network information



//    static String getMobileNetworkInfo(Context context) {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (telephonyManager != null) {
//            // Get the network type
//            int networkType = telephonyManager.getNetworkType();
//            String networkTypeName = getNetworkTypeName(networkType);
//
//            // Get the operator name
//            String operatorName = telephonyManager.getNetworkOperatorName();
//
//            // Build the network info string
//            StringBuilder networkInfo = new StringBuilder();
//            networkInfo.append("Network Type: ").append(networkTypeName).append("\n");
//            networkInfo.append("Operator Name: ").append(operatorName);
//
//            return networkInfo.toString();
//        }
//        return "TelephonyManager is null";
//    }

    // Method to get the name of the network type based on its integer value
//    private static String getNetworkTypeName(int networkType) {
//        switch (networkType) {
//            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
//                return "Unknown";
//            case TelephonyManager.NETWORK_TYPE_GPRS:
//                return "GPRS";
//            case TelephonyManager.NETWORK_TYPE_EDGE:
//                return "EDGE";
//            case TelephonyManager.NETWORK_TYPE_UMTS:
//                return "UMTS";
//            case TelephonyManager.NETWORK_TYPE_HSDPA:
//                return "HSDPA";
//            case TelephonyManager.NETWORK_TYPE_HSPA:
//                return "HSPA";
//            case TelephonyManager.NETWORK_TYPE_HSPAP:
//                return "HSPAP";
//            case TelephonyManager.NETWORK_TYPE_LTE:
//                return "LTE";
//            default:
//                return "Other";
//        }
//    }
    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtils.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtils.TYPE_WIFI) {
            status = "WIFI_ENABLED";
        } else if (conn == NetworkUtils.TYPE_MOBILE) {
            status = "MOBILE_DATA_ENABLE";
        } else if (conn == NetworkUtils.TYPE_NOT_CONNECTED) {
            status = "NOT_CONNECTED";
        }
        return status;
    }
}
