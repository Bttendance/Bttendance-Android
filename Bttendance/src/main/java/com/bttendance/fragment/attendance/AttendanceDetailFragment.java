package com.bttendance.fragment.attendance;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.socket.AttendanceUpdatedEvent;
import com.bttendance.event.socket.PostUpdatedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.feature.FeatureDetailListFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 30..
 */
public class AttendanceDetailFragment extends BTFragment {

    private int mPostID;
    private UserJson mUser;
    private CourseJson mCourse;
    private PostJson mPost;
    private boolean mAuth;

    private TextView mDate;
    private TextView mTime;
    private TextView mTitle;
    private TextView mMessage;

    private View mMoreMargin;
    private Bttendance mBttendance;

    private View mStatusLayout;
    private TextView mAttended;
    private TextView mTotal;
    private TextView mRate;

    private View mShowDetail;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long leftTime = Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt);
            if (leftTime > 60000)
                leftTime = 60000;

            if (leftTime < 0) {
                timerHandler.removeCallbacks(timerRunnable);

                String message;
                if (mAuth)
                    message = getString(R.string.attendance_message_status);
                else
                    message = getString(R.string.attendance_message_absent);

                mMessage.setText(message);
            } else {
                String message = String.format(getString(R.string.attendance_message_time_left), (int) leftTime / 1000);
                mMessage.setText(message);
                timerHandler.postDelayed(this, 200);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPostID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_POST_ID) : 0;
        mPost = BTTable.PostTable.get(mPostID);
        mUser = BTPreference.getUser(getActivity());
        mCourse = BTTable.MyCourseTable.get(mPost.course.id);
        mAuth = mUser.supervising(mCourse.id);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_detail, container, false);

        mDate = (TextView) view.findViewById(R.id.date);
        mTime = (TextView) view.findViewById(R.id.time);
        mTitle = (TextView) view.findViewById(R.id.title);
        mMessage = (TextView) view.findViewById(R.id.message);
        mMoreMargin = view.findViewById(R.id.more_margin);
        mBttendance = (Bttendance) view.findViewById(R.id.bttendance);
        mStatusLayout = view.findViewById(R.id.attendance_status);
        mAttended = (TextView) view.findViewById(R.id.attended_students);
        mTotal = (TextView) view.findViewById(R.id.total_students);
        mRate = (TextView) view.findViewById(R.id.attendance_rate);

        mShowDetail = view.findViewById(R.id.show_details_layout);
        view.findViewById(R.id.show_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeatureDetailListFragment fragment = new FeatureDetailListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BTKey.EXTRA_POST_ID, mPost.id);
                bundle.putSerializable(BTKey.EXTRA_TYPE, FeatureDetailListFragment.Type.Attendance);
                fragment.setArguments(bundle);
                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
            }
        });
        return view;
    }

    @Subscribe
    public void onAttendanceUpdated(AttendanceUpdatedEvent event) {
        reDrawView();
    }

    @Subscribe
    public void onPostUpdated(PostUpdatedEvent event) {
        reDrawView();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        if (getBTService() != null)
            getBTService().socketConnect();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        reDrawView();
    }

    private void reDrawView() {
        if (!this.isAdded() || mPost == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mPost = BTTable.PostTable.get(mPostID);
                if (mPost == null)
                    return;

                mDate.setText(DateHelper.getDateFormatString(mPost.createdAt));
                mTime.setText(DateHelper.getTimeFormatString(mPost.createdAt));

                String title1 = getString(R.string.attendance_title_auto_done);
                String title2 = getString(R.string.attendance_title_auto_ongoing);
                String title3 = getString(R.string.attendance_title_manual);

                if (mAuth) {
                    if (AttendanceJson.TYPE_MANUAL.equals(mPost.attendance.type))
                        mTitle.setText(title3);
                    else if (Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0)
                        mTitle.setText(title2);
                    else
                        mTitle.setText(title1);
                } else {

                    if (AttendanceJson.TYPE_MANUAL.equals(mPost.attendance.type))
                        mTitle.setText(title3);
                    else if (mPost.attendance.getStateInt(mUser.id) == 0
                            && Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0)
                        mTitle.setText(title2);
                    else
                        mTitle.setText(title1);
                }

                long leftTime = Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt);
                String message1 = String.format(getString(R.string.attendance_message_time_left), leftTime);
                String message2 = getString(R.string.attendance_message_status);
                String message3 = getString(R.string.attendance_message_present);
                String message4 = getString(R.string.attendance_message_absent);
                String message5 = getString(R.string.attendance_message_tardy);

                timerHandler.removeCallbacks(timerRunnable);
                if (mAuth) {
                    if (AttendanceJson.TYPE_AUTO.equals(mPost.attendance.type)
                            && Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0) {
                        mMessage.setText(message1);
                        timerHandler.postDelayed(timerRunnable, 0);
                    } else
                        mMessage.setText(message2);
                } else {
                    mMessage.setTextColor(getResources().getColor(R.color.bttendance_silver));
                    if (mPost.attendance.getStateInt(mUser.id) == 0
                            && AttendanceJson.TYPE_AUTO.equals(mPost.attendance.type)
                            && Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0) {
                        mMessage.setText(message1);
                        timerHandler.postDelayed(timerRunnable, 0);
                    } else if (mPost.attendance.getStateInt(mUser.id) == 0) {
                        mMessage.setText(message4);
                    } else if (mPost.attendance.getStateInt(mUser.id) == 1) {
                        mMessage.setText(message3);
                        mMessage.setTextColor(getResources().getColor(R.color.bttendance_navy));
                    } else if (mPost.attendance.getStateInt(mUser.id) == 2) {
                        mMessage.setText(message5);
                        mMessage.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    }
                }

                int totalStudents = 0;
                if (mCourse != null)
                    totalStudents = mCourse.students_count;
                int rate = 0;
                if (totalStudents != 0)
                    rate = Math.round((float) mPost.attendance.getAttendedCount() / (float) totalStudents * 100.0f);

                if (mAuth) {
                    mBttendance.setBttendance(Bttendance.STATE.GRADE, rate);
                } else {
                    if (mPost.attendance.getStateInt(mUser.id) == 0
                            && AttendanceJson.TYPE_AUTO.equals(mPost.attendance.type)
                            && Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0) {
                        long time = System.currentTimeMillis() - DateHelper.getTime(mPost.createdAt);
                        int progress = (int) (100.0f * (float) (Bttendance.PROGRESS_DURATION - time) / (float) Bttendance.PROGRESS_DURATION);
                        mBttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
                    } else if (mPost.attendance.getStateInt(mUser.id) == 0) {
                        mBttendance.setBttendance(Bttendance.STATE.ABSENT, 0);
                    } else if (mPost.attendance.getStateInt(mUser.id) == 1) {
                        mBttendance.setBttendance(Bttendance.STATE.PRESENT, 0);
                    } else if (mPost.attendance.getStateInt(mUser.id) == 2) {
                        mBttendance.setBttendance(Bttendance.STATE.TARDY, 0);
                    }
                }

                if (mAuth) {
                    mAttended.setText("" + mPost.attendance.getAttendedCount());
                    mTotal.setText("" + totalStudents);
                    mRate.setText(String.format(getString(R.string.rate_), rate));
                    mMoreMargin.setVisibility(View.VISIBLE);
                    mRate.setVisibility(View.VISIBLE);
                    mStatusLayout.setVisibility(View.VISIBLE);
                    mShowDetail.setVisibility(View.VISIBLE);
                } else {
                    mMoreMargin.setVisibility(View.GONE);
                    mStatusLayout.setVisibility(View.GONE);
                    mRate.setVisibility(View.GONE);
                    mShowDetail.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null || mPost == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.attendance));
        if (mAuth)
            inflater.inflate(R.menu.attendance_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_post_setting:
                String[] options = {getString(R.string.delete_attendance)};
                BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        if (getString(R.string.delete_attendance).equals(edit)) {
                            if (getBTService() != null) {
                                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.deleting_attendance)));
                                getBTService().removePost(mPost.id, new Callback<PostJson>() {
                                    @Override
                                    public void success(PostJson postJson, Response response) {
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                        getActivity().onBackPressed();
                                    }

                                    @Override
                                    public void failure(RetrofitError retrofitError) {
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCanceled() {
                    }
                }));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
