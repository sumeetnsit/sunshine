package com.example.sumeet.sunshine;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.sumeet.sunshine.data.WeatherDbHelper;

/**
 * Created by sumeet on 6/27/16.
 */
public class TestDb extends AndroidTestCase{
    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DataBase_Name);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }
}
