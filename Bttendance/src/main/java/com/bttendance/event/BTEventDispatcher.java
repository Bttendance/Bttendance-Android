package com.bttendance.event;

import android.support.v4.app.FragmentTransaction;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.event.attendance.AttdStartEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.bluetooth.BTCanceledEvent;
import com.bttendance.event.bluetooth.BTDiscoveredEvent;
import com.bttendance.event.bluetooth.BTEnabledEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    public void onAttendanceStart(final AttdStartEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        BTDialogFragment.DialogType type = BTDialogFragment.DialogType.CONFIRM;
        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.do_you_want_to_start_attendance_check);
        BTDialogFragment.OnDialogListener listener = new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {
                BTTable.ATTENDANCE_STARTING_COURSE = event.getCourseId();
                if (!BluetoothHelper.isDiscoverable()) {
                    BluetoothHelper.enableWithUI();
                    BluetoothHelper.enableDiscoverability(act);
                } else {
                    act.getBTService().postStartAttendance(BTTable.ATTENDANCE_STARTING_COURSE, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            act.getBTService().attendanceStart();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                        }
                    });
                }
            }

            @Override
            public void onCanceled() {
                BeautiToast.show(act, act.getString(R.string.attendance_check_has_been_canceled));
            }
        };
        BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, listener));
    }

    @Subscribe
    public void onAttendanceStarted(AttdStartedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (act.getSupportFragmentManager().findFragmentByTag("started") != null)
            return;

        if (BluetoothHelper.isDiscoverable()) {
            act.getBTService().attendanceStart();
            return;
        }

        BTDialogFragment.DialogType type = BTDialogFragment.DialogType.OK;
        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.turn_on_your_bluetooth_settings);
        BTDialogFragment.OnDialogListener listener = new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {
                if (!BluetoothHelper.isDiscoverable()) {
                    BluetoothHelper.enableWithUI();
                    BluetoothHelper.enableDiscoverability(act);
                } else {
                    act.getBTService().attendanceStart();
                }
            }

            @Override
            public void onCanceled() {
            }
        };
        BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, listener));
    }

    @Subscribe
    public void onBTEnabled(BTEnabledEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;
    }

    @Subscribe
    public void onBTDiscovered(BTDiscoveredEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        BTTable.UUIDLIST_add(event.getMacAddress());
    }

    @Subscribe
    public void onBTCanceled(BTCanceledEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;
    }

    @Subscribe
    public void onAddFragment(final AddFragmentEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null)
            return;

        // Hide all dialog is exist
        BTFragment frag = (BTFragment) act.getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag != null && frag instanceof BTDialogFragment)
            act.getSupportFragmentManager().popBackStackImmediate();

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BTFragment frag = event.getFragment();
                FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right);
                ft.add(R.id.content, frag, ((Object) frag).getClass().getSimpleName());
                ft.addToBackStack(((Object) frag).getClass().getSimpleName());
                ft.commitAllowingStateLoss();
            }
        });
    }

    @Subscribe
    public void onShowAlertDialog(final ShowAlertDialogEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || event.getTitle() == null || act.findViewById(R.id.content) == null)
            return;

        // Some Dialog is already exist.
        BTFragment frag = (BTFragment) act.getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag instanceof BTDialogFragment)
            ((BTDialogFragment) frag).toAlert(
                    event.getType(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getPlaceholder(),
                    event.getListener());
        else {
            FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                    R.anim.fade_in_fast,
                    R.anim.fade_out_fast,
                    R.anim.fade_in_fast,
                    R.anim.fade_out_fast);
            ft.add(R.id.content, new BTDialogFragment(
                    event.getType(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getPlaceholder(),
                    event.getListener()));
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    @Subscribe
    public void onShowContextDialog(final ShowContextDialogEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || event.getOptions() == null || act.findViewById(R.id.content) == null)
            return;

        // Some Dialog is already exist.
        BTFragment frag = (BTFragment) act.getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag instanceof BTDialogFragment && !((BTDialogFragment) frag).isDying())
            ((BTDialogFragment) frag).toContext(event.getOptions(), event.getListener());
        else {
            FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                    R.anim.fade_in_fast,
                    R.anim.fade_out_fast,
                    R.anim.fade_in_fast,
                    R.anim.fade_out_fast);
            ft.add(R.id.content, new BTDialogFragment(event.getOptions(), event.getListener()));
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    @Subscribe
    public void onShowProgressDialog(final ShowProgressDialogEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null || event.getMessage() == null)
            return;

        BTFragment frag = (BTFragment) act.getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag instanceof BTDialogFragment && !((BTDialogFragment) frag).isDying())
            ((BTDialogFragment) frag).toProgress(event.getMessage());
        else {
            FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                    R.anim.fade_in_fast,
                    R.anim.fade_out_fast,
                    R.anim.fade_in_fast,
                    R.anim.fade_out_fast);
            ft.add(R.id.content, new BTDialogFragment(event.getMessage()));
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    @Subscribe
    public void onHideProgressDialog(HideProgressDialogEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null)
            return;

        BTFragment frag = (BTFragment) act.getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag != null && frag instanceof BTDialogFragment && ((BTDialogFragment) frag).getType() == BTDialogFragment.DialogType.PROGRESS)
            act.getSupportFragmentManager().popBackStackImmediate();
    }
}
