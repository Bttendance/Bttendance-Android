package com.utopia.bttendance.event;

import android.os.Handler;
import android.support.v4.app.FragmentTransaction;

import com.squareup.otto.Subscribe;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.fragment.BTFragment;
import com.utopia.bttendance.fragment.CreateCourseFragment;
import com.utopia.bttendance.fragment.JoinCourseFragment;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.model.BTPreference;

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

    @Subscribe
    public void onAddCourse(AddCourseEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        switch (BTPreference.getUserType(act)) {
            case PROFESSOR:
                addFragment(new CreateCourseFragment());
                break;
            case STUDENT:
                addFragment(new JoinCourseFragment());
                break;
        }
    }

    private void addFragment(BTFragment frag) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right);
        ft.add(R.id.content, frag, ((Object) frag).getClass().getSimpleName());
        ft.addToBackStack(((Object) frag).getClass().getSimpleName());
        ft.commit();
    }

}
