package com.utopia.bttendance.fragment;

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
import com.squareup.otto.Subscribe;
import com.utopia.bttendance.R;
import com.utopia.bttendance.adapter.BTListAdapter;
import com.utopia.bttendance.event.MyCoursesUpdateEvent;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class JoinCourseFragment extends BTFragment {

    BTListAdapter mAdapter;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        showLoading();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_course, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
//        mListView.setFastScrollEnabled(true);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapItems();
    }

    @Subscribe
    public void onMyCourseUpdate(MyCoursesUpdateEvent event) {
        swapItems();
    }

    private void swapItems() {
        if (!this.isAdded())
            return;

        ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
        SparseArray<CourseJson> joinableCourses = BTTable.getCourses(BTTable.FILTER_JOINABLE_COURSE);
        SparseArray<CourseJson> myCourses = BTTable.getCourses(BTTable.FILTER_MY_COURSE);
        for (int i = 0; i < joinableCourses.size(); i++) {
            CourseJson course = joinableCourses.valueAt(i);
            boolean joined = myCourses.get(course.id) != null;
            String title = course.number + " " + course.name;
            String message = getString(R.string.prof_) + course.professor_name;
            items.add(new BTListAdapter.Item(false, joined, title, message, course));
        }
        Collections.sort(items, new Comparator<BTListAdapter.Item>() {
            @Override
            public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
        hideLoading();
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
        getBTService().joinableCourses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courseJsons, Response response) {
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.join_course));
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
