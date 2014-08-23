package com.bttendance.fragment.feature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.BTListAdapter;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.AttendanceJsonSimple;
import com.bttendance.model.json.ClickerJsonSimple;
import com.bttendance.model.json.NoticeJsonSimple;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJsonSimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 8. 22..
 */
public class FeatureDetailListFragment extends BTFragment implements View.OnClickListener {

    BTListAdapter mAdapter;
    private ListView mListView;
    public Type mType;
    public Sort mSort;
    public PostJson mPost;
    private UserJsonSimple[] mUsers;

    public enum Type {Clicker, Attendance, Notice}

    public enum Sort {Name, Number, Status}

    private Button mSortName;
    private Button mSortNumber;
    private Button mSortStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mType = getArguments() != null ? (Type) getArguments().getSerializable(BTKey.EXTRA_TYPE) : Type.Notice;
        mSort = getArguments() != null ? (Sort) getArguments().getSerializable(BTKey.EXTRA_SORT) : Sort.Status;
        if (mSort == null)
            mSort = Sort.Status;
        int postID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_POST_ID) : 0;
        mPost = BTTable.PostTable.get(postID);
        if (mPost != null)
            mUsers = BTPreference.getStudentsOfCourse(getActivity(), mPost.course.id).users;

        UserJsonSimple user1 = new UserJsonSimple();
        user1.id = 1;
        user1.full_name = "Victoria";
        user1.student_id = "12345678";

        UserJsonSimple user2 = new UserJsonSimple();
        user2.id = 2;
        user2.full_name = "Henry";
        user2.student_id = "00000000";

        UserJsonSimple user3 = new UserJsonSimple();
        user3.id = 3;
        user3.full_name = "Justin";
        user3.student_id = "49562983";

        UserJsonSimple user4 = new UserJsonSimple();
        user4.id = 4;
        user4.full_name = "Leonardo";
        user4.student_id = "dicafrio";

        mUsers = new UserJsonSimple[]{user1, user2, user3, user4};

        ClickerJsonSimple clicker = new ClickerJsonSimple();
        clicker.a_students = new int[]{1};
        clicker.b_students = new int[]{};
        clicker.c_students = new int[]{3};
        clicker.d_students = new int[]{};
        clicker.e_students = new int[]{};
        mPost.clicker = clicker;

        NoticeJsonSimple notice = new NoticeJsonSimple();
        notice.seen_students = new int[]{4};
        mPost.notice = notice;

        AttendanceJsonSimple attendance = new AttendanceJsonSimple();
        attendance.checked_students = new int[]{4};
        attendance.late_students = new int[]{1};
        mPost.attendance = attendance;

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feature_detail_list, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        mSortName = (Button) view.findViewById(R.id.sort_by_name);
        mSortNumber = (Button) view.findViewById(R.id.sort_by_number);
        mSortStatus = (Button) view.findViewById(R.id.sort_by_status);

        mSortName.setOnClickListener(this);
        mSortNumber.setOnClickListener(this);
        mSortStatus.setOnClickListener(this);

        switch (mSort) {
            case Name:
                mSortName.setSelected(true);
                mSortName.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                break;
            case Number:
                mSortNumber.setSelected(true);
                mSortNumber.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                break;
            case Status:
                mSortStatus.setSelected(true);
                mSortStatus.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                break;
        }

        switch (mType) {
            case Clicker:
                mSortStatus.setText(getString(R.string.sort_by_choice));
                break;
            case Attendance:
                mSortStatus.setText(getString(R.string.sort_by_status));
                break;
            case Notice:
                mSortStatus.setText(getString(R.string.sort_by_read));
                break;
        }

        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        requestCall();
        swapItems();
    }

    private void requestCall() {
        if (mPost == null)
            return;

        getBTService().courseStudents(mPost.course.id, new Callback<UserJsonSimple[]>() {
            @Override
            public void success(UserJsonSimple[] users, Response response) {
                mUsers = users;
                swapItems();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void swapItems() {
        if (!this.isAdded() || mUsers == null)
            return;

        BTListAdapter.Item.Type type;
        switch (mType) {
            case Clicker:
                type = BTListAdapter.Item.Type.CLICKER;
                break;
            case Attendance:
                type = BTListAdapter.Item.Type.ATTENDANCE;
                break;
            case Notice:
            default:
                type = BTListAdapter.Item.Type.NOTICE;
                break;
        }

        final ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
        for (UserJsonSimple user : mUsers) {
            String title = user.full_name;
            String message = user.student_id;
            items.add(new BTListAdapter.Item(type, title, message, getStatus(user.id)));
        }

        switch (mSort) {
            case Name:
                Collections.sort(items, new Comparator<BTListAdapter.Item>() {
                    @Override
                    public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                    }
                });
                break;
            case Number:
                Collections.sort(items, new Comparator<BTListAdapter.Item>() {
                    @Override
                    public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                        return lhs.getMessage().compareToIgnoreCase(rhs.getMessage());
                    }
                });
                break;
            case Status:
                Collections.sort(items, new Comparator<BTListAdapter.Item>() {
                    @Override
                    public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                    }
                });

                Collections.sort(items, new Comparator<BTListAdapter.Item>() {
                    @Override
                    public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                        return lhs.getStatus().ordinal() - rhs.getStatus().ordinal();
                    }
                });
                break;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setItems(items);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private BTListAdapter.Item.Status getStatus(int userID) {
        switch (mType) {
            case Clicker:
                int choice = mPost.clicker.getChoiceInt(userID);
                switch (choice) {
                    case 1:
                        return BTListAdapter.Item.Status.CHOICE_A;
                    case 2:
                        return BTListAdapter.Item.Status.CHOICE_B;
                    case 3:
                        return BTListAdapter.Item.Status.CHOICE_C;
                    case 4:
                        return BTListAdapter.Item.Status.CHOICE_D;
                    case 5:
                        return BTListAdapter.Item.Status.CHOICE_E;
                    case 6:
                    default:
                        return BTListAdapter.Item.Status.CHOICE_NONE;
                }
            case Attendance:
                int state = mPost.attendance.getStateInt(userID);
                switch (state) {
                    case 1:
                        return BTListAdapter.Item.Status.PRESENT;
                    case 2:
                        return BTListAdapter.Item.Status.TARDY;
                    case 0:
                    default:
                        return BTListAdapter.Item.Status.ABSENT;
                }
            case Notice:
            default:
                boolean seen = mPost.notice.seen(userID);
                if (seen)
                    return BTListAdapter.Item.Status.READ;
                else
                    return BTListAdapter.Item.Status.UNREAD;
        }
    }

    @Override
    public void onClick(View view) {

        mSortName.setSelected(false);
        mSortNumber.setSelected(false);
        mSortStatus.setSelected(false);

        mSortName.setTextColor(getResources().getColor(R.color.bttendance_navy));
        mSortNumber.setTextColor(getResources().getColor(R.color.bttendance_navy));
        mSortStatus.setTextColor(getResources().getColor(R.color.bttendance_navy));

        switch (view.getId()) {
            case R.id.sort_by_name:
                mSortName.setSelected(true);
                mSortName.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                mSort = Sort.Name;
                swapItems();
                break;
            case R.id.sort_by_number:
                mSortNumber.setSelected(true);
                mSortNumber.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                mSort = Sort.Number;
                swapItems();
                break;
            case R.id.sort_by_status:
                mSortStatus.setSelected(true);
                mSortStatus.setTextColor(getResources().getColor(R.color.bttendance_cyan));
                mSort = Sort.Status;
                swapItems();
                break;
        }
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
        switch (mType) {
            case Clicker:
                actionBar.setTitle(getString(R.string.clicker));
                break;
            case Attendance:
                actionBar.setTitle(getString(R.string.attendance));
                break;
            case Notice:
                actionBar.setTitle(getString(R.string.notice));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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