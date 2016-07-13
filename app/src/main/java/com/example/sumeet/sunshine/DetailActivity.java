package com.example.sumeet.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MyActivity";


    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        String key = getIntent().getStringExtra(DetailActivityFragment.DATE_KEY);
//        DetailActivityFragment detailActivityFragment = DetailActivityFragment.newInstance(key);
////        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_detail);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().add(R.id.content_detail, detailActivityFragment).commit();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            String date = getIntent().getStringExtra(DetailActivityFragment.DATE_KEY);

            Bundle arguments = new Bundle();
            arguments.putString(DetailActivityFragment.DATE_KEY, date);

            DetailActivityFragment fragment = DetailActivityFragment.newInstance(date);
//            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_detail, fragment)
                    .commit();
        }
    }
    private void openPreferredLocationInMap(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPrefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Uri geoLocation =Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        else {
            Log.d(LOG_TAG,"could not resolve location : " + location);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if(id == R.id.action_map){
            openPreferredLocationInMap();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }
}
