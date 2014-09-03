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
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.socket.NoticeUpdatedEvent;
import com.bttendance.event.socket.PostUpdatedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.feature.FeatureDetailListFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 27..
 */
public class NoticeDetailFragment extends BTFragment {

    private int mPostID;
    private UserJson mUser;
    private CourseJson mCourse;
    private PostJson mPost;
    private boolean mAuth;
    private TextView mGuide;
    private TextView mMessage;
    private View mShowDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPostID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_POST_ID) : 0;
        mPost = BTTable.PostTable.get(mPostID);
        mUser = BTPreference.getUser(getActivity());
        mCourse = BTTable.MyCourseTable.get(mPost.course.id);
        mAuth = mUser.supervising(mPost.course.id);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_detail, container, false);

        mGuide = (TextView) view.findViewById(R.id.notice_guide);
        mMessage = (TextView) view.findViewById(R.id.message_tv);

        mShowDetail = view.findViewById(R.id.show_details_layout);
        view.findViewById(R.id.show_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeatureDetailListFragment fragment = new FeatureDetailListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BTKey.EXTRA_POST_ID, mPost.id);
                bundle.putSerializable(BTKey.EXTRA_TYPE, FeatureDetailListFragment.Type.Notice);
                fragment.setArguments(bundle);
                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
            }
        });

        return view;
    }

    @Subscribe
    public void onNoticeUpdated(NoticeUpdatedEvent event) {
        reDrawView();
    }

    @Subscribe
    public void onPostUpdated(PostUpdatedEvent event) {
        reDrawView();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        if (getBTService() != null)
            getBTService().socketConnect();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (getBTService() != null && mPost != null && !mAuth)
            getBTService().noticeSeen(mPost.notice.id, null);

        reDrawView();
    }

    private void reDrawView() {
        if (!this.isAdded() || mPost == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mPost = BTTable.PostTable.get(mPostID);
                if (mPost == null)
                    return;

                String createdAt = DateHelper.getPostFormatString(mPost.createdAt);
                if (mAuth) {
                    int seenCount = mPost.notice.seen_students.length;

                    int studentCount = 0;
                    if (mCourse != null)
                        studentCount = mCourse.students_count;

                    int rate = 0;
                    if (studentCount != 0)
                        rate = seenCount / studentCount * 100;

                    mGuide.setText(String.format(getString(R.string.notice_detail_prof), createdAt, seenCount, studentCount, rate));
                } else if (mPost.notice.seen(mUser.id)) {
                    mGuide.setText(String.format(getString(R.string.notice_detail_std_read), createdAt));
                } else {
                    mGuide.setText(String.format(getString(R.string.notice_detail_std_unread), createdAt));
                }

                mMessage.setText(mPost.message);

                if (mAuth)
                    mShowDetail.setVisibility(View.VISIBLE);
                else
                    mShowDetail.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null || mPost == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
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
