package com.example.sumeet.sunshine;

import android.app.Application;
import android.content.Context;

/**
 * Created by sumeet on 7/5/16.
 */
public final class SunshineApp extends Application {
    public static Context APPLICATION_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APPLICATION_CONTEXT = this;
    }


}
