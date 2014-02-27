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
import com.bttendance.event.update.UpdatePostAttendanceEvent;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

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

    public PostAttendanceFragment(int postId) {
        mPost = BTTable.PostTable.get(postId);
        mCourse = BTTable.CourseTable.get(mPost.course);
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
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        requestCall();
    }

    @Subscribe
    public void onUpdate(UpdatePostAttendanceEvent event) {
        swapItems();
    }

    private void requestCall() {
        if (getBTService() == null)
            return;

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

        getBTService().post(mPost.id, new Callback<PostJson>() {
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

        mPost = BTTable.PostTable.get(mPost.id);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SparseArray<UserJson> attdChecked = new SparseArray<UserJson>();
                SparseArray<UserJson> attdUnChecked = new SparseArray<UserJson>();
                for (UserJson user : mUserJsons)
                    if (IntArrayHelper.contains(mPost.checks, user.id))
                        attdChecked.append(user.id, user);
                    else
                        attdUnChecked.append(user.id, user);

                ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();

                if (attdUnChecked.size() > 0)
                    items.add(new BTListAdapter.Item(getString(R.string.attendance_unchecked_students)));

                for (int i = 0; i < attdUnChecked.size(); i++) {
                    UserJson user = attdUnChecked.valueAt(i);
                    boolean joined = IntArrayHelper.contains(mPost.checks, user.id);
                    String title = user.full_name;
                    String message = user.email;
                    items.add(new BTListAdapter.Item(joined, title, message, user, mPost == null ? -1 : mPost.id));
                }

                if (attdChecked.size() > 0)
                    items.add(new BTListAdapter.Item(getString(R.string.attendance_checked_students)));

                for (int i = 0; i < attdChecked.size(); i++) {
                    UserJson user = attdChecked.valueAt(i);
                    boolean joined = IntArrayHelper.contains(mPost.checks, user.id);
                    String title = user.full_name;
                    String message = user.email;
                    items.add(new BTListAdapter.Item(joined, title, message, user, mPost == null ? -1 : mPost.id));
                }

//        Collections.sort(items, new Comparator<BTListAdapter.Item>() {
//            @Override
//            public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
//                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
//            }
//        });
                mAdapter.setItems(items);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
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
