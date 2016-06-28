package com.example.sumeet.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
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
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
