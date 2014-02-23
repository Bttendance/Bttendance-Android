package com.bttendance.activity;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.BTEventBus;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.sign.CatchPointActivity;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.bluetooth.BTCanceledEvent;
import com.bttendance.event.bluetooth.BTDiscoveredEvent;
import com.bttendance.event.bluetooth.BTEnabledEvent;
import com.bttendance.event.BTEventDispatcher;
import com.bttendance.event.fragment.ShowEnableGPSDialogEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.helper.GPSTracker;
import com.bttendance.model.BTNotification;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.BTService;

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
                    BTEventBus.getInstance().post(new BTDiscoveredEvent(device.getAddress()));
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
    private BTEventDispatcher mEventDispatcher = null;
    private BTService mService = null;
    private MenuItem mRefresh;
    private boolean mShowLoading = false;

    public void addOnServiceConnectListener(OnServiceConnectListener listener) {
        mServiceListeners.add(listener);
    }

    public void removeOnServiceConnectListener(OnServiceConnectListener listener) {
        mServiceListeners.remove(listener);
    }

    protected void onServieConnected() {
        checkPlayServices();
        for (OnServiceConnectListener listener : mServiceListeners)
            if (listener != null)
                listener.onServieConnected();
    }

    protected void onServieDisconnected() {
        for (OnServiceConnectListener listener : mServiceListeners)
            if (listener != null)
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

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBTConnect != null && mService != null)
            BTService.unbind(this, mBTConnect);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BTEventBus.getInstance().register(mEventDispatcher);
        EasyTracker.getInstance().activityStart(this);

        BTDebug.LogError("ScanMode : " + BluetoothHelper.getScanMode() + " onGoing : " + BTTable.getCheckingPostIds().size());
        if (BTTable.getCheckingPostIds().size() > 0)
            BTEventBus.getInstance().post(new AttdStartedEvent(true));
    }

    @Override
    protected void onStop() {
        super.onStop();
        BTEventBus.getInstance().unregister(mEventDispatcher);
        EasyTracker.getInstance().activityStop(this);
    }

    public Intent getNextIntent() {
        UserJson user = BTPreference.getUser(this);
        Intent intent;
        if (user == null || user.username == null || user.password == null) {
            BTPreference.clearUser(this);
            intent = new Intent(this, CatchPointActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    private void checkPlayServices() {
        if (!(this instanceof MainActivity))
            return;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                BTDebug.LogError("This device doesn't support play service.");
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

        if (BluetoothHelper.REQUEST_ENABLE_DISCOVERABILITY_BT == requestCode) {
            if (RESULT_CANCELED == resultCode)
                BTEventBus.getInstance().post(new BTCanceledEvent());
            else
                BTEventBus.getInstance().post(new BTEnabledEvent());
        }
    }

    public void showLoading(boolean showLoading) {
        mShowLoading = showLoading;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRefresh != null) {
                    if (mShowLoading)
                        mRefresh.setActionView(R.layout.loading_menu);
                    else
                        mRefresh.setActionView(null);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.options_menu, menu);
        mRefresh = menu.findItem(R.id.refresh_option_item);
        if (mShowLoading)
            mRefresh.setActionView(R.layout.loading_menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        showGPSDialog();
    }

    private void showGPSDialog() {
        if (this instanceof MainActivity && !GPSTracker.isGpsEnable(this))
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BTEventBus.getInstance().post(new ShowEnableGPSDialogEvent());
                }
            }, 3000);
    }

    public interface OnServiceConnectListener {
        void onServieConnected();

        void onServieDisconnected();
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
