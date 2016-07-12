package com.example.sumeet.sunshine;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

public class DetailActivity extends AppCompatActivity {


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

}
