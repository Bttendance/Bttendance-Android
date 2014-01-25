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
import com.squareup.otto.Subscribe;
import com.bttendance.R;
import com.bttendance.adapter.BTListAdapter;
import com.bttendance.event.AttdCheckedManuallyEvent;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 30..
 */
public class PostAttendanceFragment extends BTFragment {

    BTListAdapter mAdapter;
    private ListView mListView;
    private CourseJson mCourse;
    private PostJson mPost;
    private UserJson[] mUserJsons;

    public PostAttendanceFragment(int courseId) {
        mCourse = BTTable.CourseTable.get(courseId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_attendance, container, false);
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

    @Subscribe
    public void onAttdCheckedManually(AttdCheckedManuallyEvent event) {
        mPost = BTTable.PostTable.get(mPost.id);
        swapItems();
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

        if (mCourse.posts.length == 0)
            return;

        getBTService().post(mCourse.posts[mCourse.posts.length - 1], new Callback<PostJson>() {
            @Override
            public void success(PostJson postJson, Response response) {
                mPost = postJson;
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
            boolean joined = mPost != null && IntArrayHelper.contains(mPost.checks, user.id);
            String title = user.username + " - " + user.full_name;
            String message = getString(R.string.email_) + user.email;
            items.add(new BTListAdapter.Item(false, joined, title, message, user, mPost == null? -1: mPost.id));
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
        actionBar.setTitle(mCourse.name);
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
