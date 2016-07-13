package com.example.sumeet.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;


import com.example.sumeet.sunshine.data.WeatherContract;

import org.w3c.dom.Text;

import static android.support.v4.view.MenuItemCompat.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String dateString;

    public DetailActivityFragment() {
        this.setHasOptionsMenu(true);
    }

    private static final int DETAIL_LOADER = 0;
    public static final String DATE_KEY = "date";
    private static final String LOCATION_KEY = "location";
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private String mForecastStr;
    private String mLocation;
    private String mDateStr;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public static DetailActivityFragment newInstance(String key) {
        Bundle args = new Bundle();
        DetailActivityFragment fragment = new DetailActivityFragment();
        args.putString(DATE_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
       dateString = bundle.getString(DATE_KEY, "");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
        };

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, dateString);

        return new CursorLoader(getActivity(), weatherUri, FORECAST_COLUMNS, null, null, null);

    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int weatherId = data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            // Read date from cursor and update views for day of week and date
            String dateText = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));

            String friendlyDateText = Utility.getFriendlyDayString(getActivity(),dateText);
            // Read description from cursor and update view
            String description = data.getString(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));


            // Read high temperature from cursor and update view
            boolean isMetric = Utility.isMetric(getActivity());

            double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
            String highString = Utility.formatTemperature(getActivity(), high, isMetric) ;

            // Read low temperature from cursor and update view
            double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
            String lowString = Utility.formatTemperature(getActivity(), low, isMetric) ;

            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(Utility.formatDate(dateText));
            mDescriptionView.setText(description);
            mHighTempView.setText(highString);
            mLowTempView.setText(lowString);

            mForecastStr = String.format("%s - %s - %s/%s", mDateView.getText(), mDescriptionView.getText(), mHighTempView.getText(), mLowTempView.getText());

            //Read humidity from cursor and update view
            float humidity = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Read wind speed and direction from cursor and update view
            float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
            float windDirStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            // Read pressure from cursor and update view
            float pressure = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // We still need this for the share intent
//
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(createShareForecastIntent());
//            }
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }



    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_DOCUMENT));
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mshareactionprovider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mshareactionprovider != null) {
            mshareactionprovider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "share action provider is null?");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LOCATION_KEY, mLocation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            mDateStr = arguments.getString(DATE_KEY);
        }

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mIconView = (ImageView)rootView.findViewById(R.id.detail_icon);
        mFriendlyDateView = (TextView)rootView.findViewById(R.id.detail_day_textview);
        mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;

    }



}
