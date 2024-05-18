package com.rovaherve.rovaherve;

// MainActivity.java
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    try {
                        startRecordingService();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    requestPermissions();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecordingService();
            }
        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (checkPermissions()) {
//            startRecordingService();
//        } else {
//            requestPermissions();
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        stopRecordingService();
//    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSIONS);
    }
//
//getExternalFilesDir(null) + "/sdcard/audio_recording.3gp"
    private void startRecordingService() throws IOException {
        Intent serviceIntent = new Intent(this, AudioRecordingService.class);
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        // String audioFileName = "RECORDING_" + timeStamp + "_";
        // serviceIntent.putExtra("FILE_PATH", getExternalFilesDir(null)+"/"+audioFileName+".3gp");
        File audioFile = null;
        try {
            audioFile = createAudioFile();
        } catch (IOException ex) {
            Log.e("VideoCapture", "Error occurred while creating the video file", ex);
        }

        // File audioFileName = null;
        // audioFileName = audioFile();
        serviceIntent.putExtra("FILE_PATH", String.valueOf(audioFile));
        ContextCompat.startForegroundService(this, serviceIntent);

    }

    private void stopRecordingService() {
        Intent serviceIntent = new Intent(this, AudioRecordingService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                try {
                    startRecordingService();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Permission denied
            }
        }
    }

    private File createAudioFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "AUDIO_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return File.createTempFile(
                videoFileName,
                ".3gp",
                storageDir
        );
    }
}
