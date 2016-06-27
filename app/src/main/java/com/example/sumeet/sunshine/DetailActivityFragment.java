package com.example.sumeet.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;


import org.w3c.dom.Text;

import static android.support.v4.view.MenuItemCompat.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
        this.setHasOptionsMenu(true);
    }

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private String mForecastStr;

    private ArrayAdapter<String> mDetailedActivityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.detail_text)).setText(mForecastStr);
        }
        return rootView;

    }

    private Intent createShareForecastIntent(){
        Intent shareIntent= new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_DOCUMENT));
        shareIntent.putExtra(Intent.EXTRA_TEXT , mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.detailfragment,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mshareactionprovider =
                (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);

        if (mshareactionprovider != null){
            mshareactionprovider.setShareIntent(createShareForecastIntent());
        }
        else{
            Log.d(LOG_TAG,"share action provider is null?");
        }
    }

}
