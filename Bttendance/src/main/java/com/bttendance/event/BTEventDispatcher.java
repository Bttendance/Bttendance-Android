package com.bttendance.event;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.event.fragment.ShowPostAttendanceEvent;
import com.bttendance.event.update.UpdatePostAttendanceEvent;
import com.bttendance.event.attendance.AttdStartEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.bluetooth.BTCanceledEvent;
import com.bttendance.event.bluetooth.BTDiscoveredEvent;
import com.bttendance.event.bluetooth.BTEnabledEvent;
import com.bttendance.event.button.PlusClickedEvent;
import com.bttendance.event.dialog.ShowAddManagerDialog;
import com.bttendance.event.dialog.ShowEnableBluetoothDialog;
import com.bttendance.event.fragment.ShowAddManagerEvent;
import com.bttendance.event.fragment.ShowCourseAttendEvent;
import com.bttendance.event.fragment.ShowCourseCreateEvent;
import com.bttendance.event.fragment.ShowCourseDetailEvent;
import com.bttendance.event.fragment.ShowCreateNoticeEvent;
import com.bttendance.event.fragment.ShowForgotPasswordEvent;
import com.bttendance.event.fragment.ShowGradeEvent;
import com.bttendance.event.fragment.ShowSchoolChooseEvent;
import com.bttendance.event.fragment.ShowSerialEvent;
import com.bttendance.event.fragment.ShowSerialRequestEvent;
import com.bttendance.event.fragment.ShowUpdateProfileEvent;
import com.bttendance.event.refresh.RefreshCourseListEvent;
import com.bttendance.event.update.UpdateCourseAttendEvent;
import com.bttendance.event.update.UpdateProfileEvent;
import com.bttendance.fragment.AddManagerFragment;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.CourseAttendFragment;
import com.bttendance.fragment.CourseCreateFragment;
import com.bttendance.fragment.CourseDetailFragment;
import com.bttendance.fragment.CreateNoticeFragment;
import com.bttendance.fragment.ForgotPasswordFragment;
import com.bttendance.fragment.GradeFragment;
import com.bttendance.fragment.PostAttendanceFragment;
import com.bttendance.fragment.ProfileEditFragment;
import com.bttendance.fragment.SchoolChooseFragment;
import com.bttendance.fragment.SerialFragment;
import com.bttendance.fragment.SerialRequestFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.BTJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
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
            public void onConfirmed(String edit) {
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
            return;
        }

        String title = act.getString(R.string.attendance_check);
        String message = act.getString(R.string.turn_on_your_bluetooth_settings);

        final BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
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
        });
        showDialog(dialog, "started");
    }

    @Subscribe
    public void onBTEnabled(BTEnabledEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

//        if (act instanceof ProfessorActivity) {
//            if (BTTable.ATTENDANCE_STARTING_COURSE != -1) {
//                act.getBTService().postAttendanceStart(BTTable.ATTENDANCE_STARTING_COURSE, new Callback<CourseJson>() {
//                    @Override
//                    public void success(CourseJson courseJson, Response response) {
//                        act.getBTService().attendanceStart();
//                    }
//
//                    @Override
//                    public void failure(RetrofitError retrofitError) {
//                    }
//                });
//            } else
//                act.getBTService().attendanceStart();
//        } else if (act instanceof MainActivity) {
//            act.getBTService().attendanceStart();
//        }
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

//        if (act instanceof ProfessorActivity) {
//            BeautiToast.show(act, act.getString(R.string.attendance_check_has_been_canceled));
//        } else if (act instanceof MainActivity) {
//            onAttendanceStarted(new AttdStartedEvent(false));
//        }
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

//    @Subscribe
//    public void onShowEnableGPSDialog(ShowEnableGPSDialogEvent event) {
//        final BTActivity act = getBTActivity();
//        if (act == null)
//            return;
//
//        String title = act.getString(R.string.turn_on_gps_title);
//        String message = act.getString(R.string.turn_on_gps_message);
//
//        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
//        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
//            @Override
//            public void onConfirmed() {
//                GPSTracker.showLocationSettings(act);
//            }
//
//            @Override
//            public void onCanceled() {
//            }
//        });
//        showDialog(dialog, "gps");
//    }

    // For Sign Up
    @Subscribe
    public void onShowEnableBluetoothDialog(ShowEnableBluetoothDialog event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        String title = act.getString(R.string.turn_on_bt_title);
        String message = act.getString(R.string.turn_on_bt_message);

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed(String edit) {
                BluetoothHelper.enable(act);
            }

            @Override
            public void onCanceled() {
            }
        });
        showDialog(dialog, "bt");
    }

    @Subscribe
    public void onShowAddManagerDialog(final ShowAddManagerDialog event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        String title = act.getString(R.string.add_manager);
        String message = String.format(act.getString(R.string.would_you_like_to_add), event.getFullname(), event.getCoursename());

        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed(String edit) {
                act.getBTService().addManager(event.getUsername(), event.getCourseID(), new Callback<CourseJson>() {
                    @Override
                    public void success(CourseJson courseJson, Response response) {
                        BeautiToast.show(act, String.format(act.getString(R.string.is_now_manager_of_course), event.getFullname(), event.getCoursename()));
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
            }

            @Override
            public void onCanceled() {
            }
        });
        showDialog(dialog, "manager");
    }

    /**
     * Show Fragment Events
     */

    @Subscribe
    public void onShowSchoolChoose(ShowSchoolChooseEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new SchoolChooseFragment(event.getButtonID()));
    }

    @Subscribe
    public void onShowCourseCreate(ShowCourseCreateEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new CourseCreateFragment(event.getSchoolID()));
    }

    @Subscribe
    public void onShowSerial(ShowSerialEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new SerialFragment(event.getSchoolID()));
    }

    @Subscribe
    public void onShowCourseAttendEvent(ShowCourseAttendEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new CourseAttendFragment(event.getSchoolID()));
    }

    @Subscribe
    public void onShowAddManagerEvent(ShowAddManagerEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new AddManagerFragment(event.getCourseId()));
    }

    @Subscribe
    public void onShowUpdateProfile(ShowUpdateProfileEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        BTFragment fragment = new ProfileEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BTKey.EXTRA_TITLE, event.getTitle());
        bundle.putString(BTKey.EXTRA_MESSAGE, event.getMessage());
        bundle.putSerializable(BTKey.EXTRA_TYPE, event.getType());
        fragment.setArguments(bundle);
        addFragment(fragment);
    }

    @Subscribe
    public void onShowCourseDetail(ShowCourseDetailEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        BTFragment fragment = new CourseDetailFragment(event.getCourseId());
        addFragment(fragment);
    }

    @Subscribe
    public void onShowPostAttd(ShowPostAttendanceEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new PostAttendanceFragment(event.getPostId()));
    }

    @Subscribe
    public void onShowGrade(ShowGradeEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new GradeFragment(event.getCourseId()));
    }

    @Subscribe
    public void onShowForgotPassword(ShowForgotPasswordEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new ForgotPasswordFragment());
    }

    @Subscribe
    public void onShowSerialRequest(ShowSerialRequestEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new SerialRequestFragment());
    }

    @Subscribe
    public void onShowCreateNotice(ShowCreateNoticeEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        addFragment(new CreateNoticeFragment(event.getCourseId()));

    }

    @Subscribe
    public void onPlusClicked(final PlusClickedEvent event) {
        final BTActivity act = getBTActivity();
        if (act == null)
            return;

        final BTJson json = event.getJson();

        BTDialogFragment dialog;
        String title = null;
        String message = null;

        switch (json.getType()) {
            case Course:
                if (IntArrayHelper.contains(BTPreference.getUser(act).enrolled_schools, ((CourseJson) json).school)) {
                    title = act.getString(R.string.attend_course);
                    CourseJson course = (CourseJson) json;
                    message = course.number + " " + course.name + "\n"
                            + act.getString(R.string.prof_) + course.professor_name + "\n"
                            + course.school_name;
                    dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
                } else {
                    title = act.getString(R.string.student_id_or_phone_number);
                    CourseJson course = (CourseJson) json;
                    message = String.format(act.getString(R.string.before_you_join_course), course.name);
                    dialog = new BTDialogFragment(BTDialogFragment.DialogType.EDIT, title, message);
                }
                break;
            case User:
            default:
                title = act.getString(R.string.attendance_check);
                UserJson user = (UserJson) json;
                message = String.format(act.getString(R.string.do_you_want_to_approve_attendance), user.full_name);
                dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, title, message);
                break;
        }

        dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
            @Override
            public void onConfirmed(String edit) {
                switch (json.getType()) {
                    case Course:
                        if (IntArrayHelper.contains(BTPreference.getUser(act).enrolled_schools, ((CourseJson) json).school)) {
                            act.getBTService().attendCourse(json.id, new Callback<UserJson>() {
                                @Override
                                public void success(UserJson userJson, Response response) {
                                    BTEventBus.getInstance().post(new UpdateCourseAttendEvent());
                                    BTEventBus.getInstance().post(new RefreshCourseListEvent());
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                }
                            });
                        } else {
                            act.getBTService().enrollSchool(event.getId(), edit, new Callback<UserJson>() {
                                @Override
                                public void success(UserJson userJson, Response response) {
                                    BTEventBus.getInstance().post(new UpdateProfileEvent());
                                    act.getBTService().attendCourse(json.id, new Callback<UserJson>() {
                                        @Override
                                        public void success(UserJson userJson, Response response) {
                                            BTEventBus.getInstance().post(new UpdateCourseAttendEvent());
                                            BTEventBus.getInstance().post(new RefreshCourseListEvent());
                                        }

                                        @Override
                                        public void failure(RetrofitError retrofitError) {
                                        }
                                    });
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {

                                }
                            });
                        }
                        break;
                    case User:
                        act.getBTService().postAttendanceCheckManually(event.getId(), json.id, new Callback<PostJson>() {
                            @Override
                            public void success(PostJson postJson, Response response) {
                                BTEventBus.getInstance().post(new UpdatePostAttendanceEvent());
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
