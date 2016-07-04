package com.example.sumeet.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Switch;

import java.security.PrivilegedAction;

/**
 * Created by sumeet on 6/28/16.
 */
public class WeatherProvider extends ContentProvider {

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE =102;
    private static final int LOCATION =300;
    private static final int LOCATION_ID =301;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private WeatherDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
    static {
        sWeatherByLocationSettingQueryBuilder.setTables(WeatherContract.WeatherEntry.TABLE_NAME+ " INNER JOIN " + WeatherContract.LocationEntry.TABLE_NAME + " ON " +
                WeatherContract.WeatherEntry.TABLE_NAME+"."+WeatherContract.WeatherEntry.COLUMN_LOC_KEY
                + " = " + WeatherContract.LocationEntry.TABLE_NAME+"."+
                WeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection = WeatherContract.LocationEntry.TABLE_NAME+"."+
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingWithStartDateSelection = WeatherContract.LocationEntry.TABLE_NAME+"."+
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " + WeatherContract.WeatherEntry.COLUMN_DATETEXT
            + " >= ? " ;
    private static final String sLocationSettingWithDaySelection =  WeatherContract.LocationEntry.TABLE_NAME+"."+
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " + WeatherContract.WeatherEntry.COLUMN_DATETEXT
            + " = ? " ;
    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection,String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);
        String[] selectionArgs;
        String selection;

        if(startDate == null){
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else{
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[]{locationSetting, startDate};
        }
        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,sortOrder);

    }

    private Cursor getWeatherByLocationSettingWithDate(Uri uri, String[] projection,String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String day = WeatherContract.WeatherEntry.getStartDateFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sLocationSettingWithDaySelection;
        selectionArgs = new String[]{locationSetting, day};
        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,sortOrder);

    }

    private static UriMatcher buildUriMatcher(){

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority,WeatherContract.PATH_WEATHER,WEATHER);
        uriMatcher.addURI(authority,WeatherContract.PATH_WEATHER+"/*",WEATHER_WITH_LOCATION);
        uriMatcher.addURI(authority,WeatherContract.PATH_WEATHER+"/*/*",WEATHER_WITH_LOCATION_AND_DATE);
        uriMatcher.addURI(authority,WeatherContract.PATH_LOCATION,LOCATION);
        uriMatcher.addURI(authority,WeatherContract.PATH_LOCATION+"/#",LOCATION_ID);

        return uriMatcher;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor =getWeatherByLocationSettingWithDate(uri,projection,sortOrder) ;
                break;
            }
            case WEATHER_WITH_LOCATION:
            {
                retCursor = getWeatherByLocationSetting(uri, projection,sortOrder);
                break;
            }
            case WEATHER:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "location/*"
            case LOCATION_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match)
        {
            case WEATHER:
            {
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,contentValues);
                if(_id >0)
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert new row into:" + uri);
                break;
            }
            case  LOCATION:
            {
                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,contentValues);
                if(_id >0)
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert new row into:" + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted =0;
        switch (match)
        {
            case WEATHER:
            {
                rowsDeleted = db.delete(WeatherContract.WeatherEntry.TABLE_NAME,s,strings);
                break;
            }
            case  LOCATION:
            {
                rowsDeleted = db.delete(WeatherContract.LocationEntry.TABLE_NAME,s,strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri" + uri);
        }
        if(s== null||rowsDeleted !=0 )
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted ;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db =mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match)
        {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value :values){
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,value);
                        if(-1 != _id)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri,values);


        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated=0;
        switch (match)
        {
            case WEATHER:
            {
                rowsUpdated = db.update(WeatherContract.WeatherEntry.TABLE_NAME,contentValues,s,strings);
                break;
            }
            case  LOCATION:
            {
                rowsUpdated = db.update(WeatherContract.LocationEntry.TABLE_NAME,contentValues,s,strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri" + uri);
        }
        if(rowsUpdated !=0 )
            getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }
}
