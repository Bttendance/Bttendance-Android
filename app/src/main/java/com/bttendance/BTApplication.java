package com.bttendance;

import android.app.Application;
import android.content.Context;

/**
 * Created by TheFinestArtist on 2014. 8. 17..
 */
public class BTApplication extends Application {

    private static BTApplication instance;

    public static BTApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
