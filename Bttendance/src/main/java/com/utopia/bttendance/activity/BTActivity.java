package com.utopia.bttendance.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.Event.BTEventDispatcher;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.activity.sign.PersionalizeActivity;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;

import java.util.Stack;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTActivity extends SherlockFragmentActivity {

    private BTEventDispatcher mEventDispatcher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventDispatcher = new BTEventDispatcher();
        ActivityStack.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BTEventBus.getInstance().register(mEventDispatcher);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    public static class ActivityStack extends Application {

        private static Stack<SherlockFragmentActivity> classes = new Stack<SherlockFragmentActivity>();

        public static void clear(SherlockFragmentActivity activity) {
            for (final SherlockFragmentActivity act : classes) {
                if (act != null && act != activity) {
                    if (act instanceof SplashActivity) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                act.finish();
                            }
                        }, 1000);
                    } else
                        act.finish();
                }
            }
        }

        public static void add(SherlockFragmentActivity activity) {
            classes.push(activity);
        }
    }

    public Intent getNextIntent() {
        UserJson user = BTPreference.getUser(this);
        Intent intent;
        if (user == null || user.username == null || user.password == null) {
            intent = new Intent(this, CatchPointActivity.class);
        } else if (user.type == null) {
            intent = new Intent(this, PersionalizeActivity.class);
        } else if (UserJson.PROFESSOR.equals(user.type)) {
            intent = new Intent(this, ProfessorActivity.class);
        } else if (UserJson.STUDENT.equals(user.type)) {
            intent = new Intent(this, StudentActivity.class);
        } else {
            BTDebug.LogError("User Type Error : " + user.type);
            BTPreference.clearAuth(this);
            intent = new Intent(this, CatchPointActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }
}
