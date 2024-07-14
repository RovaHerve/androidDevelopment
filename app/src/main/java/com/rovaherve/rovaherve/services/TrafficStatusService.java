package com.rovaherve.rovaherve.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.rovaherve.rovaherve.R;
import com.rovaherve.rovaherve.activities.MainActivity;
import com.rovaherve.rovaherve.activities.TrafficNetworks;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import androidx.annotation.Nullable;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;

public class TrafficStatusService extends Service {

    // private static final int NOTIFICATION_ID = 123;
    // private static final String CHANNEL_ID = "MyNotificationChannel";
    // private static final String CHANNEL_NAME = "My Notification Channel";
    private static TrafficNetworks trafficUtils = new TrafficNetworks();


    @Override
    public void onCreate() {
        super.onCreate();
        // createNotificationChannel();
        // startForeground(NOTIFICATION_ID, createNotification());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateSpeed();
            }
        }, 0, 1000); // Update speed every second
    }

    private void updateSpeed() {
        String test = trafficUtils.getNetworkSpeed();
        Log.d("NetworkSpeed: ", test);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    private Notification createNotification() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        String test = TrafficNetworks.getNetworkSpeed();
//        return new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("NETWORK SPEED")
//                .setContentText(test)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                // .setContentIntent(pendingIntent)
//                .build();
//    }

//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
}

