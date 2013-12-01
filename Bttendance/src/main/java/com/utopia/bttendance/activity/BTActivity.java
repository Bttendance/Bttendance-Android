package com.utopia.bttendance.activity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.event.BTEventDispatcher;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.service.BTAPI;
import com.utopia.bttendance.service.BTService;

import java.util.Stack;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTActivity extends SherlockFragmentActivity {

    protected ServiceConnection mBTConnect = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BTDebug.LogDebug("Service Disconnected");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BTDebug.LogDebug("Service Connected");
            mService = ((BTService.LocalBinder) service).getService();
        }
    };
    private BTEventDispatcher mEventDispatcher = null;
    private BTService mService = null;

    public BTService getBTService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.add(this);
        mEventDispatcher = new BTEventDispatcher();
        BTService.bind(this, mBTConnect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBTConnect != null && mService != null)
            BTService.unbind(this, mBTConnect);
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

    public Intent getNextIntent() {
        UserJson user = BTPreference.getUser(this);
        Intent intent;
        if (user == null || user.username == null || user.password == null || user.type == null) {
            BTPreference.clearAuth(this);
            intent = new Intent(this, CatchPointActivity.class);
        } else if (BTAPI.PROFESSOR.equals(user.type)) {
            intent = new Intent(this, ProfessorActivity.class);
        } else if (BTAPI.STUDENT.equals(user.type)) {
            intent = new Intent(this, StudentActivity.class);
        } else {
            BTPreference.clearAuth(this);
            intent = new Intent(this, CatchPointActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
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
}
