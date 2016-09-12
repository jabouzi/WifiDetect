package com.skanderjabouzi.wifidetect;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WifiDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public WifiDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        Log.i("WifiDataSource", "open");
    }

    public boolean isOpen()
    {
        return database.isOpen();
    }

    public void close() {
        dbHelper.close();
    }

    void addWifi(Wifi wifi) {
        ContentValues values = new ContentValues();
        values.put("id", wifi.getId());
        values.put("bssid", wifi.getBssid());
        values.put("ssid", wifi.getSsid());
        values.put("frequency", wifi.getFrequency());
        values.put("level", wifi.getLevel());

        database.insert("wifi", null, values);
        database.close();
    }

    // Getting single wifi
    Wifi getWifi(int id) {
        Cursor cursor = database.query("wifi", new String[] { "id",
                "bssid", "ssid", "frequency", "level"}," id = ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Wifi wifi = new Wifi();
        wifi.setId(cursor.getLong(0));
        wifi.setBssid(cursor.getString(1));
        wifi.setSsid(cursor.getString(2));
        wifi.setFrequency(cursor.getInt(3));
        wifi.setLevel(cursor.getInt(4));
        cursor.close();

        // return wifi
        return wifi;
    }

    // Getting All Wifis
    public List<Wifi> getAllOptions() {
        List<Wifi> wifisList = new ArrayList<Wifi>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "wifis";
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Wifi wifi = new Wifi();
                wifi.setId(cursor.getLong(0));
                wifi.setBssid(cursor.getString(1));
                wifi.setSsid(cursor.getString(2));
                wifi.setFrequency(cursor.getInt(3));
                wifi.setLevel(cursor.getInt(4));
                // Adding options to list
                wifisList.add(wifi);
            } while (cursor.moveToNext());
        }

        // return options list
        return wifisList;
    }

    // Updating single wifi
    public int updateWifi(Wifi wifi) {
        ContentValues values = new ContentValues();
        values.put("bssid", wifi.getBssid());
        values.put("ssid", wifi.getSsid());
        values.put("frequency", wifi.getFrequency());
        values.put("level", wifi.getLevel());

        // updating row
        return database.update("wifi", values," id = ?",
                new String[] { String.valueOf(wifi.getId()) });
    }

    // Deleting single wifi
    public void deleteWifi(Wifi wifi) {
        database.delete("wifi"," id = ?",
                new String[] { String.valueOf(wifi.getId()) });
        database.close();
    }


    // Getting wifi Count
    public int getWifiCount() {
        String countQuery = "SELECT  * FROM " + "wifi";
        Cursor cursor = database.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
