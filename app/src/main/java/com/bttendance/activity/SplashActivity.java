package com.bttendance.activity;

import android.os.Bundle;
import android.os.Handler;

import com.bttendance.R;
import com.bttendance.model.BTTable;
import com.crashlytics.android.Crashlytics;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class SplashActivity extends BTActivity {

    private static final int SPLASH_DURATION = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, SPLASH_DURATION);
    }

    private void nextActivity() {
        startActivity(getNextIntent());
        overridePendingTransition(R.anim.fade_in, R.anim.splash_out);
    }
}
