package com.utopia.bttendance.event;

import android.os.Handler;

import com.squareup.otto.Subscribe;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;

import java.lang.ref.WeakReference;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTEventDispatcher {

    private WeakReference<BTActivity> mBTActRef;

    public BTEventDispatcher(BTActivity btActivity) {
        mBTActRef = new WeakReference<BTActivity>(btActivity);
    }

    private BTActivity getBTActivity() {
        if (mBTActRef == null)
            return null;
        return mBTActRef.get();
    }

    @Subscribe
    public void onCheckStart(final CheckStartEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        BluetoothHelper.enableWithUI();
        BluetoothHelper.enableDiscoverability(act);
        BluetoothHelper.startDiscovery();

        final BTActivity.OnBluetoothDiscoveryListener listener = new BTActivity.OnBluetoothDiscoveryListener() {
            @Override
            public void onBluetoothDiscoveryEnabled() {
                act.getBTService().postAttendanceStart(event.getCourseId(), null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        act.setOnBluetoothDiscoveryListener(null);
                    }
                }, BluetoothHelper.DISCOVERABILITY_BT_DURATION);
            }

            @Override
            public void onBluetoothDiscovered(String address) {

            }

            @Override
            public void onBluetoothDiscoveryCanceled() {
                BluetoothHelper.enableWithUI();
                BluetoothHelper.enableDiscoverability(act);
                BluetoothHelper.startDiscovery();
            }
        };

        act.setOnBluetoothDiscoveryListener(listener);
    }

}
