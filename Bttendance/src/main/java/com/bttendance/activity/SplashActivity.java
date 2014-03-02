package com.bttendance.activity;

import android.os.Bundle;
import android.os.Handler;

import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.adapter.BTPagerAdapter;
import com.bttendance.model.BTPreference;
import com.crashlytics.android.Crashlytics;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class SplashActivity extends BTActivity {

    private static int SPLASH_DURATION = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_splash);
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
