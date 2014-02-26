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
import com.bttendance.event.update.MyCoursesUpdateEvent;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseAttendFragment extends BTFragment {

    BTListAdapter mAdapter;
    private int mSchoolID;
    private ListView mListView;
    private UserJson user;

    public CourseAttendFragment(int schoolID) {
        mSchoolID = schoolID;
        user = BTPreference.getUser(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_attend, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapItems();
    }

    @Subscribe
    public void onUpdate(MyCoursesUpdateEvent event) {
        swapItems();
    }

    private void swapItems() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    user = BTPreference.getUser(getActivity());

                    SparseArray<CourseJson> courses = BTTable.getCoursesOfSchool(mSchoolID);
                    SparseArray<CourseJson> attendingCourses = new SparseArray<CourseJson>();
                    SparseArray<CourseJson> joinableCourses = new SparseArray<CourseJson>();
                    for (int i = 0; i < courses.size(); i++) {
                        CourseJson course = courses.valueAt(i);
                        boolean joined = IntArrayHelper.contains(user.attending_courses, course.id)
                                || IntArrayHelper.contains(user.supervising_courses, course.id);
                        if (joined)
                            attendingCourses.append(course.id, course);
                        else
                            joinableCourses.append(course.id, course);
                    }

                    ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
                    if (attendingCourses.size() > 0)
                        items.add(new BTListAdapter.Item(getString(R.string.attending_courses)));

                    for (int i = 0; i < attendingCourses.size(); i++) {
                        CourseJson course = attendingCourses.valueAt(i);
                        String title = course.number + " " + course.name;
                        String message = getString(R.string.prof_) + course.professor_name;
                        items.add(new BTListAdapter.Item(true, title, message, course, mSchoolID));
                    }

                    if (joinableCourses.size() > 0)
                        items.add(new BTListAdapter.Item(getString(R.string.joinable_courses)));

                    for (int i = 0; i < joinableCourses.size(); i++) {
                        CourseJson course = joinableCourses.valueAt(i);
                        String title = course.number + " " + course.name;
                        String message = getString(R.string.prof_) + course.professor_name;
                        items.add(new BTListAdapter.Item(false, title, message, course, mSchoolID));
                    }

//                    Collections.sort(items, new Comparator<BTListAdapter.Item>() {
//                        @Override
//                        public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
//                            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
//                        }
//                    });
                    mAdapter.setItems(items);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        getBTService().schoolCourses(mSchoolID, new Callback<CourseJson[]>() {
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
        actionBar.setTitle(getString(R.string.attend_course));
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
