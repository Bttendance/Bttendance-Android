package com.bttendance.fragment;

import android.os.Bundle;
import android.util.SparseArray;
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
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.GradeJson;
import com.bttendance.model.json.UserJson;

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
    private UserJson[] mUserJsons;
    public static SparseArray<GradeJson> GradeTable = new SparseArray<GradeJson>();

    public GradeFragment(int courseId) {
        mCourse = BTTable.CourseTable.get(courseId);
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
        mAdapter = new BTListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
//        mListView.setFastScrollEnabled(true);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        requestCall();
    }

    private void requestCall() {

        getBTService().courseStudents(mCourse.id, new Callback<UserJson[]>() {
            @Override
            public void success(UserJson[] userJsons, Response response) {
                mUserJsons = userJsons;
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });

        getBTService().courseGrades(mCourse.id, new Callback<GradeJson[]>() {
            @Override
            public void success(GradeJson[] gradeJsons, Response response) {
                for (GradeJson grade: gradeJsons)
                    GradeTable.append(grade.id, grade);
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void swapItems() {
        if (!this.isAdded() || mUserJsons == null)
            return;

        ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
        for (UserJson user : mUserJsons) {
            String title = user.username + " - " + user.full_name;
            String message = getString(R.string.email_) + user.email;
            GradeJson grade = GradeTable.get(user.id);
            items.add(new BTListAdapter.Item(false, title, message, grade == null? new GradeJson(): grade, grade == null? -1: grade.id));
        }
        Collections.sort(items, new Comparator<BTListAdapter.Item>() {
            @Override
            public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
        requestCall();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
