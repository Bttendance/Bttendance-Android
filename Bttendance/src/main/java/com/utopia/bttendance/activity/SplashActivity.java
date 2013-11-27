package com.utopia.bttendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.activity.sign.PersionalizeActivity;
import com.utopia.bttendance.helper.UUIDHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class SplashActivity extends BTActivity {

    private static int SPLASH_DURATION = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUIDHelper.getUUID(this);
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
