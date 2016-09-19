package com.skanderjabouzi.wifidetect;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.os.Bundle;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.Locale;
import java.io.IOException;
import android.app.Service;
import java.util.List;
import java.io.IOException;
import android.os.Handler;
import android.location.Geocoder;
import java.util.Arrays;
import android.widget.Toast;
import 	android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by skanderjabouzi on 2016-09-11.
 */
public class WifiService extends Service implements LocationListener{

    private static final String TAG = "WIFI SERVICE";
    public static final String LOCATION_INTENT = "com.skanderjabouzi.wifi.LOCATION_INTENT";
    public static final String LOCATION = "LOCATION";
    public static final String RECEIVE_LOCATION_NOTIFICATIONS = "com.skanderjabouzi.wifi.RECEIVE_LOCATION_NOTIFICATIONS";
    private LocationManager locationManager;
    private final Context context = WifiService.this;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    double latitude;
    double longitude;
    Location location;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private int mInterval = 5000;
    private final Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask;
    private LocationDataSource ldatasource;
    private com.skanderjabouzi.wifidetect.Location wifiLocation;
    public String bestProvider;
    public Criteria criteria;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG,"onStartCommand" );
        ldatasource = new LocationDataSource(this);
        ldatasource.open();
        getLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    public void getLocation() {
//        Log.i(TAG,"getLocation" );
        try {
            criteria = new Criteria();
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();


            if (!isGPSEnabled && !isNetworkEnabled) {
//                Log.i(TAG,"GSP AND INTERNET DISBLED" );
                sendNotification("GEO_INTERNET_DISABLED");
            }
            else
            {
//                Log.i(TAG,"GSP AND INTERNET ENABLED" );
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
//                        Log.i(TAG,"NetworkEnabled");
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
//                                Log.i(TAG,"LATITUDE1 : " + String.valueOf(location.getLatitude()));
//                                Log.i(TAG,"LONGITUDE1 : " + String.valueOf(location.getLongitude()));
                            }
                            else
                            {
//                                Log.i(TAG,"LOCATION1 IS NULL");
                                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                            }
                        }
                    }

                    if (isGPSEnabled) {
//                        Log.i(TAG,"GPSEnabled");
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
//                                    Log.i(TAG,"LATITUDE2 : " + String.valueOf(location.getLatitude()));
//                                    Log.i(TAG,"LONGITUDE2 : " + String.valueOf(location.getLongitude()));
                                }
                            }
                        }
                        else
                        {
//                            Log.i(TAG,"LOCATION2 IS NULL");
                            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                        }
                    }

                    String locationValues = String.valueOf(location.getLatitude());
                    locationValues += "|" + String.valueOf(location.getLongitude());
                    sendNotification(locationValues);
                }
                else
                {
                    sendNotification("PERMISSION_NOT_GRANTED" );

                    return;
                }
            }
        }
        catch (Exception e)
        {
            sendNotification("GEO_NULL");
            e.printStackTrace();
        }

        stopHandler();
        cleanLocation();
    }

    public void sendNotification(String extra)
    {
        Intent intent;
        intent = new Intent(LOCATION_INTENT);
        intent.putExtra(LOCATION, extra);
        sendBroadcast(intent, RECEIVE_LOCATION_NOTIFICATIONS);
        Log.i(TAG,"SEND NOTIFICATION");
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG,"destroy");
        if (ldatasource.isOpen()) ldatasource.close();
        super.onDestroy();
    }

    private void stopService()
    {
        Log.i(TAG,"stop");
        stopHandler();
        if (ldatasource.isOpen()) ldatasource.close();
        stopService(new Intent(this, WifiService.class));
    }

    public void onLocationChanged(Location location) {
        Log.i(TAG,"LocationChanged");
    }

    public void onProviderDisabled(String provider) {
        Log.i(TAG,"ProviderDisabled");
    }

    public void onProviderEnabled(String provider) {
        Log.i(TAG,"ProviderEnabled");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG,"tatusChanged");
    }

    private void cleanLocation() {
        Log.i(TAG,"cleanLocation");
        locationManager.removeUpdates(this);
        stopService();
    }

    public void stopHandler() {
        Log.i(TAG,"stopHandler");
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
}

