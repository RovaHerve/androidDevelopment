package com.rovaherve.rovaherve;

import android.Manifest;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonNext;
    static final int START = R.id.buttonStart;
    static final int STOP = R.id.buttonStop;
    static final int NEXT = R.id.buttonNext;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonNext =  findViewById(R.id.buttonNext);

//        buttonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent startIntent = new Intent(activity, MyService.class);
//                startService(startIntent);
//            }
//        });
//        buttonStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopService(new Intent(activity, MyService.class));
//            }
//        });
//        buttonNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(), NextActivity.class);
//               startActivity(intent);
//            }
//        });

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonNext.setOnClickListener(this);


    }
    public void onClick(View src) {
        // private final int SRCID = src.getId();
        if (src.getId() == START) {
            startService(new Intent(getApplicationContext(), MyService.class));
        } else if (src.getId() == STOP) {
            stopService(new Intent(getApplicationContext(), MyService.class));
        } else if (src.getId() == NEXT) {
            Intent intent = new Intent(activity, NextActivity.class);
            startActivity(intent);
        }
    }
}