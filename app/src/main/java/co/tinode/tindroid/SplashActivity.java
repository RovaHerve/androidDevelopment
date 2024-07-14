package co.tinode.tindroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import co.tinode.tindroid.db.BaseDb;
import co.tinode.tindroid.services.TrafficStatusService;

/**
 * Splash screen on startup
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);

        // No need to check for live connection here.

        // Send user to appropriate screen:
        // 1. If we have an account and no credential validation is needed, send to ChatsActivity.
        // 2. If we don't have an account or credential validation is required send to LoginActivity.
//        Intent launch = new Intent(this, BaseDb.getInstance().isReady() ?
//                ChatsActivity.class : LoginActivity.class);
//        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(launch);
//        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
//        startActivity(login);
//        finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startService(new Intent(getApplicationContext(), TrafficStatusService.class));
        Toast.makeText(this, "network service enabled", Toast.LENGTH_SHORT).show();

        // Delay for 3 seconds before launching the SecondActivity
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Launch SecondActivity after 3 seconds
//                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish MainActivity after launching SecondActivity
            }
        }, 4000); // 3000 milliseconds = 3 seconds
    }

}
