package com.utopia.bttendance.event;

import android.support.v4.app.FragmentTransaction;

import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.activity.ProfessorActivity;
import com.utopia.bttendance.activity.StudentActivity;
import com.utopia.bttendance.fragment.BTDialogFragment;
import com.utopia.bttendance.fragment.BTFragment;
import com.utopia.bttendance.fragment.CreateCourseFragment;
import com.utopia.bttendance.fragment.JoinCourseFragment;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.helper.GPSTracker;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.BTJson;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.view.BeautiToast;

import java.lang.ref.WeakReference;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTEventDispatcher {

    private WeakReference<BTActivity> mBTActRef;
    /**
     * Loading
     */
    private int mLoading = 0;

    public BTEventDispatcher(BTActivity btActivity) {
        mBTActRef = new WeakReference<BTActivity>(btActivity);
    }

    private BTActivity getBTActivity() {
        if (mBTActRef == null)
            return null;
        return mBTActRef.get();
    }

    // Professor start attendance
    @Subscribe
    public void onAttendanceStart(final AttdStartEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.do_you_wish_to_start_attendance_check);

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
                BTTable.ATTENDANCE_STARTING_COURSE = event.getCourseId();
                BluetoothHelper.enableWithUI();
                BluetoothHelper.enableDiscoverability(act);
            }

            @Override
            public void onCanceled() {
                BeautiToast.show(act, act.getString(R.string.attendance_check_has_been_canceled));
            }
        });
        showDialog(dialog);
    }

    // Student attendance started
    @Subscribe
    public void onAttendanceStarted(AttdStartedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || !(act instanceof StudentActivity))
            return;

        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.turn_on_your_bluetooth_settings);

        final BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
                BluetoothHelper.enableWithUI();
                BluetoothHelper.enableDiscoverability(act);
            }

            @Override
            public void onCanceled() {
            }
        });
        showDialog(dialog);
    }

    @Subscribe
    public void onBTEnabledEvent(BTEnabledEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (act instanceof ProfessorActivity) {
            act.getBTService().postAttendanceStart(BTTable.ATTENDANCE_STARTING_COURSE, new Callback<CourseJson>() {
                @Override
                public void success(CourseJson courseJson, Response response) {
                    BluetoothHelper.startDiscovery();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                }
            });
        } else if (act instanceof StudentActivity) {
            BluetoothHelper.startDiscovery();
        }
    }

    @Subscribe
    public void onBTDiscoveredEvent(BTDiscoveredEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        Set<Integer> ids = BTTable.getCheckingPostIds();
        for (int i : ids) {
            act.getBTService().postAttendanceCheck(i, act.getLocation(), BluetoothHelper.getMacAddress(), new Callback<PostJson>() {
                @Override
                public void success(PostJson postJson, Response response) {
                    BTDebug.LogInfo(postJson.toJson());
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                }
            });
        }
    }

    @Subscribe
    public void onBTCanceledEvent(BTCanceledEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (act instanceof ProfessorActivity) {
            BeautiToast.show(act, act.getString(R.string.attendance_check_has_been_canceled));
        } else if (act instanceof StudentActivity) {
            onAttendanceStarted(new AttdStartedEvent());
        }
    }

    @Subscribe
    public void onShowEnableGPSDialog(ShowEnableGPSDialogEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        String title = act.getString(R.string.turn_on_gps_title);
        String message = act.getString(R.string.turn_on_gps_message);

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
                GPSTracker.showLocationSettings(act);
            }

            @Override
            public void onCanceled() {
            }
        });
        showDialog(dialog);

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

    @Subscribe
    public void onJoin(JoinEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        final BTJson json = event.getJson();

        String title = null;
        String message = null;

        switch (json.getType()) {
            case Course:
                title = act.getString(R.string.join_course);
                CourseJson course = (CourseJson) json;
                message = course.number + " " + course.name + "\n"
                        + act.getString(R.string.prof_) + course.professor_name + "\n"
                        + course.school_name;
                break;
            case School:
                break;
        }

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
                switch (json.getType()) {
                    case Course:
                        act.getBTService().joinCourse(json.id, new Callback<UserJson>() {
                            @Override
                            public void success(UserJson userJson, Response response) {
                                BTEventBus.getInstance().post(new MyCoursesUpdateEvent());
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                            }
                        });
                        break;
                    case School:
                        break;
                }
            }

            @Override
            public void onCanceled() {

            }
        });
        showDialog(dialog);
    }

    private void addFragment(BTFragment frag) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null)
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

    private void showDialog(BTDialogFragment dialog) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null)
            return;

        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.fade_in_fast,
                R.anim.fade_out_fast,
                R.anim.fade_in_fast,
                R.anim.fade_out_fast);
        ft.add(R.id.content, dialog, ((Object) dialog).getClass().getSimpleName());
        ft.addToBackStack(((Object) dialog).getClass().getSimpleName());
        ft.commit();
    }

    @Subscribe
    public void onLoadingEvent(LoadingEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (event.getVisibility())
            mLoading++;
        else
            mLoading--;

        act.showLoading(mLoading > 0);
    }
}
