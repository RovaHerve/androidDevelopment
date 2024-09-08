package co.tinode.tindroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import co.tinode.tindroid.Const;



/**
 * public class CollectDeviceInformation extends Service {
 * public CollectDeviceInformation() {
 * }
 * 
 * @Override
 *           public IBinder onBind(Intent intent) {
 *           // TODO: Return the communication channel to the service.
 *           throw new UnsupportedOperationException("Not yet implemented");
 *           }
 *           }
 **/

public class CollectDeviceInformation extends Service {

    private static final String TAG = "DeviceInfoService";
    OkHttpClient client = new OkHttpClient();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Collect device information and log it
        // logDeviceInfo();
        return START_NOT_STICKY;
    }

    private HashMap<String, HashMap<String, String>> logDeviceInfo() {
        // Device ID
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

        // IP Address
        String ipAddress = getIpAddress();

        // Storage Information
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long availableStorage = availableBlocks * blockSize; // in bytes

        HashMap<String, String> deviceInfo = new HashMap<String, String>();
        HashMap<String, String> deviceInformation = new HashMap<String, String>();
        HashMap<String, String> idMap = new HashMap<String, String>();

        deviceInformation.put("manufacturer", manufacturer);
        deviceInformation.put("model", model);
        deviceInformation.put("androidVersion", androidVersion);
        deviceInformation.put("hardware", hardware);
        deviceInformation.put("processor", processor);
        deviceInformation.put("screenResolution", screenWidth + "x" + screenHeight);
        deviceInformation.put("networkType", networkType);
        deviceInformation.put("ipAddress", ipAddress);
        deviceInformation.put("availableStorage", availableStorage + " bytes");

        HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
        idMap.put("deviceId", deviceId);

        result.put("id", idMap);
        result.put("data", deviceInformation);

        // sendDeviceInfoToServer(result);

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

    private void sendDeviceInfoToServer(HashMap<String, HashMap<String, String>> result) {
        Gson gson = new Gson();
        String json = gson.toJson(result);

        // JSONObject jsonObject = new JSONObject(json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(Const.SERVER_ENDPOINT)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error sending device info to server: " + response.code());
                } else {
                    Log.d(TAG, "Device info sent to server successfully");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error sending device info to server", e);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON object", e);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }
}

