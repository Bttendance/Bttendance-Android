package com.bttendance.fragment.attendance;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.feature.FeatureDetailListFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.PostJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class AttendanceStartFragment extends BTFragment {

    private int mCourseID;
    private String mType = null;
    private Button mAttdBtButton;
    private Button mAttdNoBtButton;
    private View mAlertBg;
    private TextView mAttendanceGuide;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCourseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_start, container, false);
        mAttdBtButton = (Button) view.findViewById(R.id.attendance_bluetooth_bt);
        mAttdBtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAttdBtButton.setSelected(true);
                mAttdNoBtButton.setSelected(false);
                mType = AttendanceJson.TYPE_AUTO;
            }
        });

        mAttdNoBtButton = (Button) view.findViewById(R.id.attendance_no_bluetooth_bt);
        mAttdNoBtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAttdBtButton.setSelected(false);
                mAttdNoBtButton.setSelected(true);
                mType = AttendanceJson.TYPE_MANUAL;
            }
        });

        mAlertBg = view.findViewById(R.id.alert_bg);
        mAttendanceGuide = (TextView) view.findViewById(R.id.attendance_guide);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.start_attendance));
        inflater.inflate(R.menu.attendance_start_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_start:
                if (AttendanceJson.TYPE_AUTO.equals(mType) || AttendanceJson.TYPE_MANUAL.equals(mType)) {
                    item.setEnabled(false);
                    BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.starting_attendance)));
                    getBTService().postStartAttendance(mCourseID, mType, new Callback<PostJson>() {
                        @Override
                        public void success(PostJson postJson, Response response) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                            if (AttendanceStartFragment.this.getActivity() != null)
                                AttendanceStartFragment.this.getActivity().onBackPressed();

                            if (AttendanceJson.TYPE_AUTO.equals(postJson.attendance.type)) {
                                AttendanceDetailFragment frag = new AttendanceDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt(BTKey.EXTRA_POST_ID, postJson.id);
                                frag.setArguments(bundle);
                                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                            }

                            if (AttendanceJson.TYPE_MANUAL.equals(postJson.attendance.type)) {
                                FeatureDetailListFragment fragment = new FeatureDetailListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt(BTKey.EXTRA_POST_ID, postJson.id);
                                bundle.putSerializable(BTKey.EXTRA_TYPE, FeatureDetailListFragment.Type.Attendance);
                                bundle.putSerializable(BTKey.EXTRA_SORT, FeatureDetailListFragment.Sort.Number);
                                fragment.setArguments(bundle);
                                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            }
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            item.setEnabled(true);
                            BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        }
                    });
                    return true;
                } else {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    mAlertBg.setBackgroundColor(getResources().getColor(R.color.bttendance_red_10));
                    mAttendanceGuide.setTextColor(getResources().getColor(R.color.bttendance_red));
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
