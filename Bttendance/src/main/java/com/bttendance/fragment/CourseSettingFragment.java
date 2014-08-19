package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.bttendance.R;
import com.bttendance.adapter.CourseSettingAdapter;
import com.bttendance.event.update.UpdateUserEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseSettingFragment extends BTFragment implements AdapterView.OnItemClickListener {

    int mCourseID;
    ListView mListView;
    CourseSettingAdapter mAdapter;

    public CourseSettingFragment(int courseID) {
        mCourseID = courseID;
    }

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        if (getBTService() != null)
            getBTService().autoSignin(null);
    }

    @Subscribe
    public void onUpdate(UpdateUserEvent event) {
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
    }

    private void showStudentList() {
    }

    private void exportRecords() {
    }

    private void clickerRecords() {
    }

    private void attendanceRecords() {
    }

    private void closeCourse() {
    }
}
