package co.tinode.tindroid.services;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.Service;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.content.Intent;
import android.view.WindowManager;

import co.tinode.tindroid.Const;
import co.tinode.tindroid.SafeWebSocketsListner;
import co.tinode.tindroid.TrafficNetworks;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.telephony.TelephonyManager;

import android.location.LocationListener;
import android.location.LocationManager;


public class TrafficStatusService extends Service {
    private static final String TAG = "DeviceInfoService";

    private static TrafficNetworks trafficUtils = new TrafficNetworks();
    private OkHttpClient client;
    private WebSocket webSocket;
    private Gson gson = new Gson();

    private HashMap<String, String> deviceInformation = new HashMap<String, String>();
    private HashMap<String, String> idMap = new HashMap<String, String>();
    private static boolean isFistOpen = true;

    private LocationManager locationManager;
    private LocationListener locationListener;



    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient();
        Request request = new Request.Builder().url(Const.SERVER_ENDPOINT).build();
        SafeWebSocketsListner listener = new SafeWebSocketsListner();
        webSocket = client.newWebSocket(request, listener);
        Timer timer = new Timer();

        // send once connected device information
        HashMap<String, HashMap<String, String>> data = getDeviceInformation();
        String deviceInfoInJson = gson.toJson(data);
        webSocket.send(deviceInfoInJson);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateSpeed();
            }
        }, 0, 1000); // Update speed every second
    }


    private void updateSpeed() {
        String NetworkSpeed = trafficUtils.getNetworkSpeed();
        HashMap<String, String> payload = new HashMap<String, String>();
        // Network Information
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        String networkType = activeNetwork != null ? activeNetwork.getTypeName() : "No Network";
        int networkSubtype = activeNetwork != null ? activeNetwork.getSubtype() : -1;
        String networkStatus = getNetworkStatus(networkType, networkSubtype);

        payload.put("time", getTime());
        payload.put("model", deviceInformation.get("model"));
        payload.put("networkSpeed", NetworkSpeed);
        payload.put("networkType", deviceInformation.get("networkType"));
        payload.put("networkStatus", getNetworkStatus(networkType, networkSubtype));
        payload.put("isConnected", "online");
        // send once connected device information
        if (isFistOpen) {

            HashMap<String, HashMap<String, String>> data = getDeviceInformation();
            isFistOpen = false;

        } else {
            String networkSpeed = gson.toJson(payload);

            webSocket.send(networkSpeed);
        }




        Log.d("NetworkSpeedLog: ", NetworkSpeed);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFistOpen = true;
    }

    private HashMap<String, HashMap<String, String>> getDeviceInformation() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toString();

        // Manufacturer and Model
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        // Android Version and SDK Version
        String androidVersion = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;

        // Hardware and Processor Information
        String hardware = Build.HARDWARE;
        String processor = Build.SUPPORTED_ABIS[0];

        // Screen Resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Network Information
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        String networkType = activeNetwork != null ? activeNetwork.getTypeName() : "No Network";
        int networkSubtype = activeNetwork != null ? activeNetwork.getSubtype() : -1;
        String networkStatus = getNetworkStatus(networkType, networkSubtype);


        // IP Address
        String ipAddress = getIpAddress();

        // Storage Information
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long availableStorage = availableBlocks * blockSize; // in bytes

        deviceInformation.put("time", getTime());
        deviceInformation.put("date", getDateTime());
        deviceInformation.put("manufacturer", manufacturer);
        deviceInformation.put("model", model);
        deviceInformation.put("androidVersion", androidVersion);
        deviceInformation.put("hardware", hardware);
        deviceInformation.put("processor", processor);
        deviceInformation.put("screenResolution", screenWidth + "x" + screenHeight);
        deviceInformation.put("networkType", networkType);
        deviceInformation.put("availableStorage", availableStorage + " bytes");
        deviceInformation.put("ipAddress", ipAddress);
        deviceInformation.put("compression", "activate");
        deviceInformation.put("isConnected", "online");
        deviceInformation.put("encryption", "activate");
        // deviceInformation.put("networkStatus", networkStatus);

        // Log.d("NETWORKTYPE", networkStatus);


        HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
        idMap.put("deviceId", deviceId);

        result.put("id", idMap);
        result.put("data", deviceInformation);

        return result;
    }

    private String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error getting IP address", e);
        }
        return "Unavailable";
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return timeFormat.format(date);
    }


    private String getNetworkStatus(String networkType, int networkSubtype) {
        if (networkType.equals("WIFI")) {
            return "WIFI";
        } else if (networkType.equals("MOBILE")) {
            switch (networkSubtype) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "3G";
                default:
                    return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}