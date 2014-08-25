package com.bttendance.event;

import android.support.v4.app.FragmentTransaction;

import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.MainActivity;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.bluetooth.BTCanceledEvent;
import com.bttendance.event.bluetooth.BTDiscoveredEvent;
import com.bttendance.event.bluetooth.BTEnabledEvent;
import com.bttendance.event.clicker.ClickerClickEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.main.ResetMainFragmentEvent;
import com.bttendance.event.notification.NotificationReceived;
import com.bttendance.event.update.UserUpdatedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.ClickerJson;
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

        if (BTTable.ATTENDANCE_STARTING_COURSE != -1) {
            int courseID = BTTable.ATTENDANCE_STARTING_COURSE;
            BTTable.ATTENDANCE_STARTING_COURSE = -1;
            act.getBTService().postStartAttendance(courseID, "auto", new Callback<PostJson>() {
                @Override
                public void success(PostJson postJson, Response response) {
                    act.getBTService().attendanceStart();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                }
            });
        } else
            act.getBTService().attendanceStart();
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

        if (BTTable.ATTENDANCE_STARTING_COURSE != -1) {
            BTTable.ATTENDANCE_STARTING_COURSE = -1;
            BeautiToast.show(act, act.getString(R.string.attendance_check_has_been_canceled));
        }
    }

    @Subscribe
    public void onClickerClicked(ClickerClickEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        PostJson post = BTTable.PostTable.get(event.getPostID());
        act.getBTService().clickerClick(post.clicker.id, event.getChoice(), new Callback<ClickerJson>() {
            @Override
            public void success(ClickerJson clickerJson, Response response) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Subscribe
    public void onResetMainFragment(ResetMainFragmentEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null || !(act instanceof MainActivity))
            return;

        ((MainActivity) act).setResetCourseID(event.getCourseID());
    }

    @Subscribe
    public void onUpdate(UserUpdatedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null || !(act instanceof MainActivity))
            return;

        ((MainActivity) act).refreshSideList();
    }

    @Subscribe
    public void onNotificationReceived(final NotificationReceived event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null || !act.isVisible())
            return;

        if (BTPreference.getUser(act) != null
                && event.getCourseID() != null
                && !BTPreference.getUser(act).supervising(Integer.parseInt(event.getCourseID()))
                && ("attendance_started".equals(event.getType())
                || "attendance_checked".equals(event.getType())
                || "clicker_started".equals(event.getType())
                || "notice".equals(event.getType())
                || "added_as_manager".equals(event.getType()))) {
            BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.OK, event.getTitle(), event.getMessage(), new BTDialogFragment.OnDialogListener() {
                @Override
                public void onConfirmed(String edit) {
                    if (!(act instanceof MainActivity))
                        act.finish();

                    BTEventBus.getInstance().post(new ResetMainFragmentEvent(Integer.parseInt(event.getCourseID())));
                }

                @Override
                public void onCanceled() {
                }
            }));
        }

        if ("added_as_manager".equals(event.getType())) {
            if (act.getBTService() != null) {
                act.getBTService().socketConnectToServer();
                act.getBTService().autoSignin(null);
            }
        }
    }

    @Subscribe
    public void onAddFragment(final AddFragmentEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null || !act.isVisible())
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
        if (act == null || event.getTitle() == null || act.findViewById(R.id.content) == null || !act.isVisible())
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
        if (act == null || event.getOptions() == null || act.findViewById(R.id.content) == null || !act.isVisible())
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
        if (act == null || act.findViewById(R.id.content) == null || event.getMessage() == null || !act.isVisible())
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
        if (act == null || act.findViewById(R.id.content) == null || !act.isVisible())
            return;

        BTFragment frag = (BTFragment) act.getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag != null && frag instanceof BTDialogFragment && ((BTDialogFragment) frag).getType() == BTDialogFragment.DialogType.PROGRESS)
            act.getSupportFragmentManager().popBackStackImmediate();
    }
}
