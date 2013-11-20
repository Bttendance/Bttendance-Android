package com.utopia.bttendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.activity.sign.PersionalizeActivity;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class SplashActivity extends BTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 500);
    }

    private void nextActivity() {
        UserJson user = BTPreference.getUser(this);
        Intent intent;
        if (user == null || user.username == null || user.password == null) {
            intent = new Intent(SplashActivity.this, CatchPointActivity.class);
        } else if (user.type == null) {
            intent = new Intent(SplashActivity.this, PersionalizeActivity.class);
        } else if (UserJson.PROFESSOR.equals(user.type)) {
            intent = new Intent(SplashActivity.this, ProfessorActivity.class);
        } else if (UserJson.STUDENT.equals(user.type)) {
            intent = new Intent(SplashActivity.this, StudentActivity.class);
        } else {
            BTDebug.LogError("User Type Error : " + user.type);
            BTPreference.clearAuth(this);
            intent = new Intent(SplashActivity.this, CatchPointActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.no_anim, R.anim.splash_out);
    }
}
