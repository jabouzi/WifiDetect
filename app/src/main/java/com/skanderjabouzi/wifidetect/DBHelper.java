package com.skanderjabouzi.wifidetect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wifidetect.db";
    private static final int DATABASE_VERSION = 1;

    private static final String WIFI_CREATE =
    "CREATE TABLE wifi (id INTEGER PRIMARY KEY AUTOINCREMENT, bssid TEXT NOT NULL, ssid TEXT NOT NULL, frequency INT NOT NULL, level INT NOT NULL); ";
    private static final String LOCATION_CREATE =
    "CREATE TABLE location (id INTEGER PRIMARY KEY AUTOINCREMENT, wifi_id INT NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL); ";
    Context dBcontext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dBcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(WIFI_CREATE);
        database.execSQL(LOCATION_CREATE);
        Log.i("DBHelper", "DB Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        //dBcontext.deleteDatabase("salat.db");
        //sdb.execSQL("DROP TABLE IF EXISTS options; DROP TABLE IF EXISTS location;");
        onCreate(db);
    }
}
