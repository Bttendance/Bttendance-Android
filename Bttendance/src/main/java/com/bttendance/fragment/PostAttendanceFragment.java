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
import com.bttendance.event.ShowAlertDialogEvent;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.helper.SparceArrayHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJsonSimple;
import com.squareup.otto.BTEventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 30..
 */
public class PostAttendanceFragment extends BTFragment implements View.OnClickListener {

    BTListAdapter mAdapter;
    private ListView mListView;
    private PostJson mPost;
    private ArrayList<UserJsonSimple> mUsers;

    public PostAttendanceFragment(int postId) {
        mPost = BTTable.PostTable.get(postId);
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
        mAdapter = new BTListAdapter(getActivity(), this);
        mListView.setAdapter(mAdapter);
        swapItems();
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        requestCall();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        requestCall();
    }

    private void requestCall() {
        if (getBTService() == null)
            return;

        getBTService().courseStudents(mPost.course.id, new Callback<UserJsonSimple[]>() {
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

        mPost = BTTable.PostTable.get(mPost.id);
        mUsers = SparceArrayHelper.asArrayList(BTTable.getStudentsOfCourse(mPost.course.id));

        Collections.sort(mUsers, new Comparator<UserJsonSimple>() {
            @Override
            public int compare(UserJsonSimple lhs, UserJsonSimple rhs) {
                return lhs.student_id.compareToIgnoreCase(rhs.student_id);
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<UserJsonSimple> attdChecked = new ArrayList<UserJsonSimple>();
                ArrayList<UserJsonSimple> attdUnChecked = new ArrayList<UserJsonSimple>();
                for (UserJsonSimple user : mUsers)
                    if (IntArrayHelper.contains(mPost.attendance.checked_students, user.id))
                        attdChecked.add(user);
                    else
                        attdUnChecked.add(user);

                ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();

                if (attdUnChecked.size() > 0)
                    items.add(new BTListAdapter.Item(getString(R.string.attendance_unchecked_students)));

                for (UserJsonSimple user : attdUnChecked) {
                    String title = user.full_name;
                    String message = user.email;
                    items.add(new BTListAdapter.Item(BTListAdapter.Item.Type.UNCHECKED, title, message, user));
                }

                if (attdChecked.size() > 0)
                    items.add(new BTListAdapter.Item(getString(R.string.attendance_checked_students)));

                for (UserJsonSimple user : attdChecked) {
                    String title = user.full_name;
                    String message = user.email;
                    items.add(new BTListAdapter.Item(BTListAdapter.Item.Type.CHECKED, title, message, user));
                }

                mAdapter.setItems(items);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:

                final UserJsonSimple user = (UserJsonSimple) v.getTag(R.id.json);

                BTDialogFragment.DialogType type = BTDialogFragment.DialogType.CONFIRM;
                String title = getString(R.string.attendance_check);
                String message = String.format(getString(R.string.do_you_want_to_approve_attendance), user.full_name);
                BTDialogFragment.OnConfirmListener listener = new BTDialogFragment.OnConfirmListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        getBTService().attendanceCheckManually(mPost.attendance.id, user.id, new Callback<AttendanceJson>() {
                            @Override
                            public void success(AttendanceJson attenanceJson, Response response) {
                                swapItems();
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                    }
                };
                BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, listener));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(mPost.course.name);
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
