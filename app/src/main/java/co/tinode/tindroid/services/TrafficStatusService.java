package co.tinode.tindroid.services;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import androidx.annotation.Nullable;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;
import co.tinode.tindroid.TrafficNetworks;

public class TrafficStatusService extends Service {
    private static TrafficNetworks trafficUtils = new TrafficNetworks();

    @Override
    public void onCreate() {
        super.onCreate();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateSpeed();
            }
        }, 0, 1000); // Update speed every second
    }

    private void updateSpeed() {
        String NetworkSpeed = trafficUtils.getNetworkSpeed();
        Log.d("NetworkSpeedLog: ", NetworkSpeed);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}