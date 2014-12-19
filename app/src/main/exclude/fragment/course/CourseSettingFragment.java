package com.bttendance.fragment.course;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import com.bttendance.R;
import com.bttendance.adapter.CourseSettingAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.CourseJsonSimple;
import com.bttendance.model.json.EmailJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseSettingFragment extends BTFragment implements AdapterView.OnItemClickListener {

    int mCourseID;
    CourseJsonSimple mCourse;
    ListView mListView;
    CourseSettingAdapter mAdapter;

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCourseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        mCourse = BTPreference.getUser(getActivity()).getCourse(mCourseID);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.course_setting));
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_setting, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new CourseSettingAdapter(getActivity(), mCourseID);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshAdapter();
    }

    private void refreshAdapter() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refreshAdapter();
                }
            });
        }
    }

    /**
     * ************************
     * onItemClick
     * *************************
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (mAdapter.getItem(position).getType()) {
            case AddManager:
                addManager();
                break;
            case ShowStudentList:
                showStudentList();
                break;
            case ExportRecords:
                exportRecords();
                break;
            case ClickerRecords:
                clickerRecords();
                break;
            case AttendanceRecords:
                attendanceRecords();
                break;
            case CloseCourse:
                closeCourse();
                break;
            case Manager:
            case Header:
            case Section:
            case Margin:
                break;
        }
    }

    /**
     * Private Methods
     */
    private void addManager() {
        AddManagerFragment fragment = new AddManagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourseID);
        fragment.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
    }

    private void showStudentList() {
        StudentRecordFragment frag = new StudentRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourseID);
        bundle.putSerializable(BTKey.EXTRA_TYPE, StudentRecordFragment.RecordType.NoRecord);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void exportRecords() {
        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.exporting_grades)));
        getBTService().courseExportGrades(mCourseID, new Callback<EmailJson>() {
            @Override
            public void success(EmailJson email, Response response) {
                BTDialogFragment.DialogType type = BTDialogFragment.DialogType.OK;
                String title = getString(R.string.export_grades);
                String message = String.format(getString(R.string.exporting_grade_has_been_finished), email.email);
                BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message));
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }
        });
    }

    private void clickerRecords() {
        StudentRecordFragment frag = new StudentRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourseID);
        bundle.putSerializable(BTKey.EXTRA_TYPE, StudentRecordFragment.RecordType.Clicker);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void attendanceRecords() {
        StudentRecordFragment frag = new StudentRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourseID);
        bundle.putSerializable(BTKey.EXTRA_TYPE, StudentRecordFragment.RecordType.Attendance);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void closeCourse() {
        String title = getString(R.string.close_course);
        String message = String.format(getString(R.string.would_you_like_to_close_course), mCourse.name);
        BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.CONFIRM, title, message, new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {
                if (getBTService() == null)
                    return;

                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.closing_course)));
                getBTService().closeCourse(mCourseID, new Callback<UserJson>() {
                    @Override
                    public void success(UserJson userJson, Response response) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                    }
                });
            }

            @Override
            public void onCanceled() {
            }
        }));
    }
}
