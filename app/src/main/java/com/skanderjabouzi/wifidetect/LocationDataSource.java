package com.skanderjabouzi.wifidetect;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public LocationDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        Log.i("LocationDataSource", "open");
    }

    public boolean isOpen()
    {
        return database.isOpen();
    }

    public void close() {
        dbHelper.close();
    }

    void addLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put("id", location.getId());
        values.put("latitude", location.getLatitude());
        values.put("longitude", location.getLongitude());

        database.insert("location", null, values);
        database.close();
    }

    // Getting single location
    Location getLocation(int id) {
        Cursor cursor = database.query("location", new String[] { "id",
                "latitude", "longitude", "city", "country"}," id = ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Location location = new Location();
        location.setId(cursor.getLong(0));
        location.setLatitude(cursor.getFloat(1));
        location.setLongitude(cursor.getFloat(2));
        cursor.close();

        // return location
        return location;
    }

    // Getting All Locations
    public List<Location> getAllOptions() {
        List<Location> locationsList = new ArrayList<Location>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "locations";
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setId(cursor.getLong(0));
                location.setLatitude(cursor.getInt(1));
                location.setLongitude(cursor.getInt(2));
                // Adding options to list
                locationsList.add(location);
            } while (cursor.moveToNext());
        }

        // return options list
        return locationsList;
    }

    // Updating single location
    public int updateLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put("latitude", location.getLatitude());
        values.put("longitude", location.getLongitude());

        // updating row
        return database.update("location", values," id = ?",
                new String[] { String.valueOf(location.getId()) });
    }

    // Deleting single location
    public void deleteLocation(Location location) {
        database.delete("location"," id = ?",
                new String[] { String.valueOf(location.getId()) });
        database.close();
    }


    // Getting location Count
    public int getLocationCount() {
        String countQuery = "SELECT  * FROM " + "location";
        Cursor cursor = database.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
