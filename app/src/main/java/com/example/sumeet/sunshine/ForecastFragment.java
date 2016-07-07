package com.example.sumeet.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sumeet.sunshine.data.WeatherContract;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.sumeet.sunshine.R.string.pref_location_key;

/**
 * A placeholder fragment containing a simple view.
 */


public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "MyActivity";
    private String mLocation;
    public static final int FORECAST_LOADER = 0;

    private  static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME+"."+WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

    public ForecastFragment() {
    }
    private SimpleCursorAdapter mForecastAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER,null,this);
    }

    @Override
    public void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setHasOptionsMenu(true);
        updateWeather();

    }
    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)){
            getLoaderManager().restartLoader(FORECAST_LOADER,null,this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = prefs.getString(getString(pref_location_key),getString(R.string.pref_location_default));
            fetchWeatherTask.execute(location);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        weatherTask.execute(location);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        mForecastAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                new String[]{
                        WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
                },
                new int[]{
                        R.id.list_item_date_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_high_textview,
                        R.id.list_item_low_textview
                },
                0
                );
                mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        switch(columnIndex){
                            case COL_WEATHER_MAX_TEMP:
                            case COL_WEATHER_MIN_TEMP:{
                                boolean isMetric = Utility.isMetric(getActivity());
                                ((TextView)view).setText(Utility.formatTemperature(getActivity(),cursor.getDouble(columnIndex),isMetric));
                                return true;
                            }
                            case COL_WEATHER_DATE:{
                                String dateString = cursor.getString(columnIndex);
                                TextView dateView = (TextView) view;
                                dateView.setText(Utility.formatDate(dateString));
                                return true;
                            }
                        }
                        return false;
                    }
                });


        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SimpleCursorAdapter adapter =(SimpleCursorAdapter)adapterView.getAdapter();
                Cursor cursor = adapter.getCursor();
                if(null != cursor && cursor.moveToPosition(position)){
                    boolean isMetric = Utility.isMetric(getActivity());
                        String forecast = String.format("%s - %s - &s/%s",
                        Utility.formatDate(cursor.getString(COL_WEATHER_DATE)),
                        cursor.getString(COL_WEATHER_DESC),
                        Utility.formatTemperature(getActivity(),cursor.getDouble(COL_WEATHER_MAX_TEMP),isMetric),
                        Utility.formatTemperature(getActivity(),cursor.getDouble(COL_WEATHER_MIN_TEMP),isMetric));
                    Intent detailed_activity = new Intent(getActivity(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT,forecast);
                    startActivity(detailed_activity);
                }


            }
        });
        return rootView;
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,

        * so for convenience we're breaking it out into its own method now.
        *
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format_date = new SimpleDateFormat("E, MMM d");
        return format_date.format(date).toString();
    }

    **
     * Prepare the weather high/lows for presentation.
     *
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.

        SharedPreferences Sharedprefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = Sharedprefs.getString(
                getString(R.string.pref_units_key),
                getString(R.string.pref_units_metric));
        if(unitType.equals(getString(R.string.pref_units_imperial))){
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }else if(!unitType.equals(getString(R.string.pref_units_metric))) {
            Log.v(LOG_TAG, "unit type not fouund" + unitType);
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }


    **
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String startDate =WeatherContract.getDbDateString(new Date());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT+" ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(mLocation,startDate);

        Log.d("ForecastFragment","Uri : "+weatherForLocationUri.toString() );

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}


