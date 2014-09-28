package com.bttendance.fragment.course;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.adapter.BTListAdapter;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.UserJsonSimple;
import com.bttendance.model.json.UserJsonSimpleArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 25..
 */
public class StudentRecordFragment extends BTFragment {

    BTListAdapter mAdapter;
    private ListView mListView;
    private int mCourseID;
    private RecordType mType;
    private UserJsonSimple[] mUsers;
    private UserJsonSimpleArray mStudents;

    public enum RecordType {NoRecord, Clicker, Attendance}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCourseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        mType = getArguments() != null ? (RecordType) getArguments().getSerializable(BTKey.EXTRA_TYPE) : RecordType.NoRecord;
        mStudents = BTPreference.getStudentsOfCourse(getActivity(), mCourseID);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_record, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        requestCall();
        swapItems();
    }

    private void requestCall() {
        if (mCourseID == 0)
            return;

        switch (mType) {
            case NoRecord:
                getBTService().courseStudents(mCourseID, new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        mUsers = users;
                        swapItems();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
                break;
            case Clicker:
                getBTService().courseClickerGrades(mCourseID, new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        mUsers = users;
                        swapItems();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
                break;
            case Attendance:
                getBTService().courseAttendanceGrades(mCourseID, new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        mUsers = users;
                        swapItems();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                    }
                });
                break;
        }
    }

    private void swapItems() {
        if (!this.isAdded())
            return;

        BTListAdapter.Item.Type type;
        switch (mType) {
            case NoRecord:
                type = BTListAdapter.Item.Type.EMPTY;
                break;
            case Clicker:
            case Attendance:
            default:
                type = BTListAdapter.Item.Type.GRADE;
                break;
        }

        if (mUsers == null) {
            if (mStudents == null || mStudents.users != null)
                return;

            ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
            for (UserJsonSimple user : mStudents.users) {
                String title = user.full_name;
                String message = user.student_id;
                items.add(new BTListAdapter.Item(type, title, message, user));
            }
            Collections.sort(items, new Comparator<BTListAdapter.Item>() {
                @Override
                public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                    return lhs.getMessage().compareToIgnoreCase(rhs.getMessage());
                }
            });
            mAdapter.setItems(items);
        } else {
            ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
            for (UserJsonSimple user : mUsers) {
                String title = user.full_name;
                String message = user.student_id;
                if (user.grade == null)
                    user.grade = "0/0";
                items.add(new BTListAdapter.Item(type, title, message, user));
            }
            Collections.sort(items, new Comparator<BTListAdapter.Item>() {
                @Override
                public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                    return lhs.getMessage().compareToIgnoreCase(rhs.getMessage());
                }
            });
            mAdapter.setItems(items);
        }

        mAdapter.notifyDataSetChanged();
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
        switch (mType) {
            case NoRecord:
                actionBar.setTitle(getString(R.string.student_list));
                break;
            case Clicker:
                actionBar.setTitle(getString(R.string.clicker_records));
                break;
            case Attendance:
                actionBar.setTitle(getString(R.string.attendance_records));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
