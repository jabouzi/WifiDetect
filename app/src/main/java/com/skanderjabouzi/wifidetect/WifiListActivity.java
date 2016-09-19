package com.skanderjabouzi.wifidetect;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.pm.PackageManager;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class WifiListActivity extends AppCompatActivity {

    private Button btnDetectWifi;
    static final String SEND_LOCATION_NOTIFICATIONS = "com.skanderjabouzi.wifi.SEND_LOCATION_NOTIFICATIONS";
    private Context context = WifiListActivity.this;
    private Intent locationIntent;
    LocationReceiver receiver;
    IntentFilter filter;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private LocationDataSource datasource;
    private Location location;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        receiver = new LocationReceiver();
        filter = new IntentFilter( WifiService.LOCATION_INTENT );
        datasource = new LocationDataSource(this);
        datasource.open();
        location = new Location();
        addListenerOnButton();
        Log.i("CODE M",String.valueOf(Build.VERSION_CODES.M));
        Log.i("CURRENT SDK",String.valueOf(Build.VERSION.SDK_INT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.registerReceiver(receiver, filter, SEND_LOCATION_NOTIFICATIONS, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        WifiListActivity.this.finish();
        unregisterReceiver(receiver);
//        datasource.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        WifiListActivity.this.finish();
//        datasource.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        WifiListActivity.this.finish();
//        datasource.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startService(new Intent(context, WifiService.class));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void addListenerOnButton() {

        btnDetectWifi = (Button) findViewById(R.id.buttonWifi);
        btnDetectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(context, WifiService.class));
            }

        });

    }

    public void showSettingsAlert(int type) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Localisation error");
        alertDialog.setMessage("Please enable Internet and try again");
        if (type == 1) alertDialog.setMessage("Please enable GPS or Internet and try again");

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String extraString = intent.getStringExtra("LOCATION");
            Log.i("EXTRA STRING : " , extraString);
            if (extraString.equals("GEO_NULL"))
            {
                showSettingsAlert(1);
            }
            else if (extraString.equals("LOCATION_NULL"))
            {
                showSettingsAlert(0);
            }
            else if (extraString.equals("PERMISSION_NOT_GRANTED"))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
            else
            {

                String[] geolocation = extraString.split("\\|");
                location.setLatitude(Float.parseFloat(geolocation[0]));
                location.setLongitude(Float.parseFloat(geolocation[1]));
                Log.i("LATITUDE : " , String.valueOf(location.getLatitude()));
                Log.i("LONGITUDE : " , String.valueOf(location.getLongitude()));
//                location.setCity(geolocation[2]);
//                location.setCountry(geolocation[3]);
//                setLocationTexts();
            }
        }
    }
}
