package com.utopia.bttendance.activity;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.sign.CatchPointActivity;
import com.utopia.bttendance.event.BTEventDispatcher;
import com.utopia.bttendance.fragment.BTFragment;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.model.BTNotification;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.service.BTAPI;
import com.utopia.bttendance.service.BTService;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTActivity extends SherlockFragmentActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static BTActivity mActivity;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null)
                    if (mBluetoothListener != null)
                        mBluetoothListener.onBluetoothDiscovered(device.getAddress());
            }
        }
    };
    protected ServiceConnection mBTConnect = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            onServieDisconnected();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((BTService.LocalBinder) service).getService();
            onServieConnected();
        }
    };
    ArrayList<OnServiceConnectListener> mServiceListeners = new ArrayList<OnServiceConnectListener>();
    OnBluetoothDiscoveryListener mBluetoothListener;
    private BTEventDispatcher mEventDispatcher = null;
    private BTService mService = null;

    public void addOnServiceConnectListener(OnServiceConnectListener listener) {
        mServiceListeners.add(listener);
    }

    public void removeOnServiceConnectListener(OnServiceConnectListener listener) {
        mServiceListeners.remove(listener);
    }

    public void setOnBluetoothDiscoveryListener(OnBluetoothDiscoveryListener listener) {
        mBluetoothListener = listener;
    }

    protected void onServieConnected() {
        checkPlayServices();
        for (OnServiceConnectListener listener : mServiceListeners)
            listener.onServieConnected();
    }

    protected void onServieDisconnected() {
        for (OnServiceConnectListener listener : mServiceListeners)
            listener.onServieDisconnected();
    }

    public BTService getBTService() {
        return mService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        ActivityStack.add(mActivity);
        mEventDispatcher = new BTEventDispatcher(this);
        BTService.bind(this, mBTConnect);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    BTFragment frag = (BTFragment) manager.findFragmentById(R.id.content);
                    if (frag != null)
                        frag.onFragmentResume();
                }
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public Intent getNextIntent() {
        UserJson user = BTPreference.getUser(this);
        Intent intent;
        if (user == null || user.username == null || user.password == null || user.type == null) {
            BTPreference.clearUser(this);
            intent = new Intent(this, CatchPointActivity.class);
        } else if (BTAPI.PROFESSOR.equals(user.type)) {
            intent = new Intent(this, ProfessorActivity.class);
        } else if (BTAPI.STUDENT.equals(user.type)) {
            intent = new Intent(this, StudentActivity.class);
        } else {
            BTPreference.clearUser(this);
            intent = new Intent(this, CatchPointActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    private void checkPlayServices() {
        if (!(this instanceof ProfessorActivity || this instanceof StudentActivity))
            return;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                BTDebug.LogError("This device is not supported.");
                finish();
            }
        } else {
            String regId = BTNotification.getRegistrationId(this);
            UserJson user = BTPreference.getUser(this);
            if (regId == null)
                BTNotification.registerInBackground(this);
            else if (!regId.equals(user.notification_key))
                BTNotification.sendRegistrationIdToBackend(this, regId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (BluetoothHelper.REQUEST_ENABLE_DISCOVERABILITY_BT == requestCode && BluetoothHelper.DISCOVERABILITY_BT_DURATION == resultCode) {
            if (mBluetoothListener != null)
                mBluetoothListener.onBluetoothDiscoveryEnabled();
        }

        if (BluetoothHelper.REQUEST_ENABLE_DISCOVERABILITY_BT == requestCode && RESULT_CANCELED == resultCode) {
            if (mBluetoothListener != null)
                mBluetoothListener.onBluetoothDiscoveryCanceled();
        }
    }

    public interface OnServiceConnectListener {
        void onServieConnected();

        void onServieDisconnected();
    }

    public interface OnBluetoothDiscoveryListener {
        void onBluetoothDiscoveryEnabled();

        void onBluetoothDiscovered(String address);

        void onBluetoothDiscoveryCanceled();
    }

    public static class ActivityStack extends Application {

        private static Stack<SherlockFragmentActivity> classes = new Stack<SherlockFragmentActivity>();

        public static void clear(SherlockFragmentActivity activity) {
            for (final SherlockFragmentActivity act : classes) {
                if (act != null && act != activity) {
                    act.finish();
                }
            }
        }

        public static void add(SherlockFragmentActivity activity) {
            classes.push(activity);
        }
    }
}
