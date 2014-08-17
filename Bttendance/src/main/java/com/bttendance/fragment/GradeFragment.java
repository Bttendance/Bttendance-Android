package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.BTListAdapter;
import com.bttendance.helper.SparseArrayHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJsonSimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 25..
 */
public class GradeFragment extends BTFragment {

    BTListAdapter mAdapter;
    private ListView mListView;
    private CourseJson mCourse;
    private ArrayList<UserJsonSimple> mUsers;

    public GradeFragment(int courseId) {
        mCourse = BTTable.MyCourseTable.get(courseId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        requestCall();
    }

    private void requestCall() {

        getBTService().courseAttendanceGrades(mCourse.id, new Callback<UserJsonSimple[]>() {
            @Override
            public void success(UserJsonSimple[] users, Response response) {
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void swapItems() {
        if (!this.isAdded())
            return;

        mUsers = SparseArrayHelper.asArrayList(BTTable.getStudentsOfCourse(mCourse.id));

        ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
        for (UserJsonSimple user : mUsers) {
            String title = user.full_name;
            String message = user.student_id;
            if (user.grade == null)
                user.grade = "0/0";
            items.add(new BTListAdapter.Item(BTListAdapter.Item.Type.GRADE, title, message, user));
        }
        Collections.sort(items, new Comparator<BTListAdapter.Item>() {
            @Override
            public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                return lhs.getMessage().compareToIgnoreCase(rhs.getMessage());
            }
        });
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        requestCall();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.grade));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
