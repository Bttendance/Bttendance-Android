package com.bttendance.fragment.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class NoticeDetailFragment extends BTFragment {

    private UserJson mUser;
    private CourseJson mCourse;
    private PostJson mPost;
    private boolean mAuth;
    private TextView mMessage;

    public NoticeDetailFragment(int postID) {
        mPost = BTTable.PostTable.get(postID);
        mUser = BTPreference.getUser(getActivity());
        mCourse = BTTable.MyCourseTable.get(mPost.course.id);
        mAuth = mUser.supervising(mCourse.id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_detail, container, false);

        String createdAt = DateHelper.getBTFormatString(mPost.createdAt);

        TextView guide = (TextView) view.findViewById(R.id.notice_guide);
        if (mAuth) {
            int seenCount = mPost.notice.seen_students.length;

            int studentCount = 0;
            if (mCourse != null)
                studentCount = mCourse.students_count;

            int rate = 0;
            if (studentCount != 0)
                rate = seenCount / studentCount * 100;

            guide.setText(String.format(getString(R.string.notice_detail_prof), createdAt, seenCount, studentCount, rate));
        } else if (mPost.notice.seen(mUser.id)) {
            guide.setText(String.format(getString(R.string.notice_detail_std_read), createdAt));
        } else {
            guide.setText(String.format(getString(R.string.notice_detail_std_unread), createdAt));
        }

        mMessage = (TextView) view.findViewById(R.id.message_tv);
        mMessage.setText(mPost.message);

        if (!mAuth)
            view.findViewById(R.id.show_details_layout).setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (getBTService() != null && !mAuth)
            getBTService().noticeSeen(mPost.notice.id, null);
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
        actionBar.setTitle(getString(R.string.notice));
        if (mAuth)
            inflater.inflate(R.menu.notice_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_post_setting:
                String[] options = {getString(R.string.delete_notice)};
                BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        if (getString(R.string.delete_notice).equals(edit)) {
                            if (getBTService() != null) {
                                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.deleting_notice)));
                                getBTService().removePost(mPost.id, new Callback<PostJson>() {
                                    @Override
                                    public void success(PostJson postJson, Response response) {
                                        BTTable.PostTable.delete(postJson.id);
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                        getActivity().onBackPressed();
                                    }

                                    @Override
                                    public void failure(RetrofitError retrofitError) {
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCanceled() {
                    }
                }));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
