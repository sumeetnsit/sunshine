package com.example.sumeet.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.sumeet.sunshine.data.WeatherContract;
import com.example.sumeet.sunshine.data.WeatherDbHelper;

/**
 * Created by sumeet on 6/27/16.
 */
public class TestProvider extends AndroidTestCase{


    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DataBase_Name);

    }
    public void testInsertReadProvider()
    {
        String testName = "NorthPole";
        String testLocationSetting = "99705";
        Double testLat = 64.772 ;
        Double testLong = -147.355;

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,testName);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,testLat);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,testLong);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,testLocationSetting);

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,values);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG,"New Row id:" + locationRowId);

        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME,
                columns,
                null,null,null,null,null);

        if(cursor.moveToFirst()){

            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);
            int nameIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
            String name = cursor.getString(nameIndex);
            int latIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
            double latitude = cursor.getDouble(latIndex);
            int longIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
            double longitude = cursor.getDouble(longIndex);

            assertEquals(testName, name);
            assertEquals(testLocationSetting,location);
            assertEquals(testLat,latitude);
            assertEquals(testLong,longitude);
        }
        else{
            fail("NO VALUES REtURNED ");
        }
    }
    public void testGetType(){
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE,type);
        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE,type);
    }


}
