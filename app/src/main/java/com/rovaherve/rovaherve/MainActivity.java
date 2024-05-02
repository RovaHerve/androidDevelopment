package com.rovaherve.rovaherve;

import android.Manifest;
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

//import androidx.appcompat.app.ContextCompat;
public class MainActivity extends AppCompatActivity  implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int REQUEST_READ_PHONE_STATE = 225;
    TextView tvPhoneDetails;
    Button btnGetDetails;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        tvPhoneDetails = findViewById(R.id.tvphonedetails);
        btnGetDetails = findViewById(R.id.btnGetDetails);
        btnGetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
                if (status != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
                } else {
                    tvPhoneDetails.setText(getDeviceInfo());
                    String url = "https://www.google.com";
                    Intent intent = new Intent(activity, WifiControlActivity.class);
                    // Intent intentImplicit = new Intent("https://google.com");
                    Intent intentImplicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    // startActivity(intent);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        // Start the activity
                        startActivity(intentImplicit);
                    }

                }
            }
        });
    }

    public String getDeviceInfo() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        String details = null;
        // String TODO = "Your are not Granted";
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }

            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
//        }
        details =
                        "NetworkOperator() : " + manager.getNetworkOperator() + "\n"
                        + "NetworkOperatorName : " + manager.getNetworkOperatorName() + "\n"
                        + "NetworkType : " + getNetworkTypeName(manager.getNetworkType());
        Log.d("SALIMO",details);
        return details;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[],  int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("SAMSUNG", "Permission Granted");
                    //Proceed to next steps

                } else {
                    Log.e("SAMSUNG", "Permission Denied");
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static String getNetworkTypeName(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA or H";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPAP or H+";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE or 4G";
            default:
                return "Other";
        }
    }
}