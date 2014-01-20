package com.bttendance.event;

import android.support.v4.app.FragmentTransaction;

import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.ProfessorActivity;
import com.bttendance.activity.StudentActivity;
import com.bttendance.fragment.AttendanceListFragment;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.CreateCourseFragment;
import com.bttendance.fragment.JoinCourseFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.helper.GPSTracker;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.BTJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BeautiToast;

import java.lang.ref.WeakReference;

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
        String message = act.getString(R.string.do_you_want_to_start_attendance_check);

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
                BTTable.ATTENDANCE_STARTING_COURSE = event.getCourseId();
                if (!BluetoothHelper.isDiscoverable()) {
                    BluetoothHelper.enableWithUI();
                    BluetoothHelper.enableDiscoverability(act);
                } else {
                    act.getBTService().postAttendanceStart(BTTable.ATTENDANCE_STARTING_COURSE, new Callback<CourseJson>() {
                        @Override
                        public void success(CourseJson courseJson, Response response) {
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
        });
        showDialog(dialog, "start");
    }

    // Professor start attendance
    @Subscribe
    public void onAttendanceInProgress(final AttdInProgressEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (act.getSupportFragmentManager().findFragmentByTag("in progress") != null)
            return;

        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.attendance_checking_is_in_progress);

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
            }

            @Override
            public void onCanceled() {
            }
        });
        showDialog(dialog, "in progress");
    }

    // Student Professor attendance started
    @Subscribe
    public void onAttendanceStarted(AttdStartedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (act.getSupportFragmentManager().findFragmentByTag("started") != null)
            return;

        if (BluetoothHelper.isDiscoverable()) {
            act.getBTService().attendanceStart();
            if (event.onGoingCheck())
                onAttendanceInProgress(new AttdInProgressEvent());
            return;
        }

        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.turn_on_your_bluetooth_settings);

        final BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
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
        });
        showDialog(dialog, "started");
    }

    // Student attendance checked
    @Subscribe
    public void onAttendanceChecked(AttdCheckedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null || !(act instanceof StudentActivity))
            return;

        if (act.getSupportFragmentManager().findFragmentByTag("checked") != null)
            return;

        String title = act.getString(R.string.attendance_check);
        String message = String.format(act.getString(R.string.attendance_has_been_checked), event.getTitle());

        final BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed() {
            }

            @Override
            public void onCanceled() {
            }
        });
        showDialog(dialog, "checked");
    }

    @Subscribe
    public void onBTEnabled(BTEnabledEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        if (act instanceof ProfessorActivity) {
            if (BTTable.ATTENDANCE_STARTING_COURSE != -1) {
                act.getBTService().postAttendanceStart(BTTable.ATTENDANCE_STARTING_COURSE, new Callback<CourseJson>() {
                    @Override
                    public void success(CourseJson courseJson, Response response) {
                        act.getBTService().attendanceStart();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
            } else
                act.getBTService().attendanceStart();
        } else if (act instanceof StudentActivity) {
            act.getBTService().attendanceStart();
        }
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

        if (act instanceof ProfessorActivity) {
            BeautiToast.show(act, act.getString(R.string.attendance_check_has_been_canceled));
        } else if (act instanceof StudentActivity) {
            onAttendanceStarted(new AttdStartedEvent(false));
        }
    }

    @Subscribe
    public void onLocationChanged(LocationChangedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

//        Set<Integer> ids = BTTable.getCheckingPostIds();
//        for (int i : ids) {
//            act.getBTService().postAttendanceCurrentLocation(i, event.getLocation(), new Callback<PostJson>() {
//                @Override
//                public void success(PostJson postJson, Response response) {
//                    BTDebug.LogInfo(postJson.toJson());
//                }
//
//                @Override
//                public void failure(RetrofitError retrofitError) {
//
//                }
//            });
//        }
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
        showDialog(dialog, "gps");
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
    public void onShowAttdList(ShowAttdListEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        switch (BTPreference.getUserType(act)) {
            case PROFESSOR:
                addFragment(new AttendanceListFragment(event.getCourseId()));
                break;
            case STUDENT:
                break;
        }
    }

    @Subscribe
    public void onPlusClicked(final PlusClickedEvent event) {
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
            case User:
                title = act.getString(R.string.attendance_check);
                UserJson user = (UserJson) json;
                message = String.format(act.getString(R.string.do_you_want_to_approve_attendance), user.full_name);
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
                    case User:
                        BTDebug.LogError("postAttendanceCheckManually : " + event.getId() + " : " + json.id);
                        act.getBTService().postAttendanceCheckManually(event.getId(), json.id, new Callback<PostJson>() {
                            @Override
                            public void success(PostJson postJson, Response response) {
                                BTEventBus.getInstance().post(new AttdCheckedManuallyEvent());
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                            }
                        });
                        break;
                }
            }

            @Override
            public void onCanceled() {

            }
        });
        showDialog(dialog, "plus");
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
        ft.commitAllowingStateLoss();
    }

    private void showDialog(BTDialogFragment dialog, String name) {
        final BTActivity act = getBTActivity();
        if (act == null || act.findViewById(R.id.content) == null)
            return;

        FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.fade_in_fast,
                R.anim.fade_out_fast,
                R.anim.fade_in_fast,
                R.anim.fade_out_fast);
        ft.add(R.id.content, dialog, name);
        ft.addToBackStack(name);
        ft.commitAllowingStateLoss();
    }
}