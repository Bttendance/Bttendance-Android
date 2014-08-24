package com.bttendance.fragment.course;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.activity.MainActivity;
import com.bttendance.adapter.FeedAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.event.socket.AttendanceUpdatedEvent;
import com.bttendance.event.socket.ClickerUpdatedEvent;
import com.bttendance.event.socket.NoticeUpdatedEvent;
import com.bttendance.event.socket.PostUpdatedEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.event.update.UpdateFeedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.attendance.AttendanceStartFragment;
import com.bttendance.fragment.clicker.ClickerStartFragment;
import com.bttendance.fragment.notice.NoticePostFragment;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.ScreenHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.PostCursor;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.CourseJsonSimple;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.PostJsonArray;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseDetailFragment extends BTFragment implements View.OnClickListener {

    ListView mListView;
    FeedAdapter mAdapter;
    View header;
    boolean mAuth;
    UserJson mUser;
    CourseJsonSimple mCourse;

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        int courseID = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        mUser = BTPreference.getUser(getActivity());
        mAuth = mUser.supervising(courseID);
        mCourse = mUser.getCourse(courseID);
        if (mCourse != null && mCourse.opened)
            BTPreference.setLastSeenCourse(getActivity(), mCourse.id);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null || mCourse == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (mCourse.opened) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            if (!((MainActivity) getActivity()).isDrawerOpen()) {
                actionBar.setTitle(mCourse.name);
                inflater.inflate(R.menu.course_detail_menu, menu);
            }
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mCourse.name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_course_setting:
                if (mAuth && mCourse.opened) {
                    CourseSettingFragment fragment = new CourseSettingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourse.id);
                    fragment.setArguments(bundle);
                    BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                } else if (mAuth) {
                    String[] options = {getString(R.string.open_course)};
                    BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                        @Override
                        public void onConfirmed(String edit) {
                            if (getString(R.string.open_course).equals(edit))
                                openCourse();
                        }

                        @Override
                        public void onCanceled() {
                        }
                    }));
                } else if (mCourse.opened) {
                    String[] options = {getString(R.string.unjoin_course)};
                    BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                        @Override
                        public void onConfirmed(String edit) {
                            if (getString(R.string.unjoin_course).equals(edit))
                                unjoinCourse();
                        }

                        @Override
                        public void onCanceled() {
                        }
                    }));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        header = inflater.inflate(R.layout.course_header, null, false);
        refreshHeader();
        mListView.addHeaderView(header);
        header.findViewById(R.id.clicker_bt).setOnClickListener(this);
        header.findViewById(R.id.attendance_bt).setOnClickListener(this);
        header.findViewById(R.id.notice_bt).setOnClickListener(this);
        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addFooterView(padding);
        mAdapter = new FeedAdapter(getActivity(), null, mCourse.id);
        mListView.setAdapter(mAdapter);
        swapCursor();
        return view;
    }

    @Subscribe
    public void onUpdate(UpdateFeedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onRefresh(RefreshFeedEvent event) {
        getFeed();
    }

    @Subscribe
    public void onClickerUpdated(ClickerUpdatedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onAttendanceUpdated(AttendanceUpdatedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onNoticeUpdated(NoticeUpdatedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onPostUpdated(PostUpdatedEvent event) {
        swapCursor();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        getFeed();
        swapCursor();
    }

    public void getFeed() {
        if (getBTService() == null || mCourse == null)
            return;

        getBTService().courseFeed(mCourse.id, 0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                swapCursor();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void refreshHeader() {
        if (!this.isAdded() || header == null || mCourse == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mAuth)
                    header.findViewById(R.id.manager_layout).setVisibility(View.GONE);

                CourseJson course = BTTable.MyCourseTable.get(mCourse.id);

                TextView courseCode = (TextView) header.findViewById(R.id.code_text);
                String code = course.code;
                if (code != null)
                    code = code.toUpperCase();
                courseCode.setText(code);

                TextView courseName = (TextView) header.findViewById(R.id.course_name);
                courseName.setText(mCourse.name);

                String studentsCount = "";
                if (course != null)
                    studentsCount = String.format("%d", course.students_count);

                String schoolName = getString(R.string.empty_school_name);
                if (mUser.getSchool(mCourse.school) != null)
                    schoolName = mUser.getSchool(mCourse.school).name;

                String detailed = mCourse.professor_name
                        + " | " + schoolName
                        + " | " + String.format(getString(R.string.s_students), studentsCount);

                Rect bounds = new Rect();
                Paint paint = new Paint();
                paint.setTextSize(12.0f);
                paint.getTextBounds(detailed, 0, detailed.length(), bounds);
                float width = DipPixelHelper.getPixel(getActivity(), (float) Math.ceil(bounds.width()));
                float textViewWidth = ScreenHelper.getWidth(getActivity()) - DipPixelHelper.getPixel(getActivity(), 42);

                if (width > textViewWidth) {
                    detailed = schoolName + " | " + String.format(getString(R.string.s_students), studentsCount);
                    paint.getTextBounds(detailed, 0, detailed.length(), bounds);
                    width = DipPixelHelper.getPixel(getActivity(), (float) Math.ceil(bounds.width()));
                    textViewWidth = ScreenHelper.getWidth(getActivity()) - DipPixelHelper.getPixel(getActivity(), 42);

                    if (width > textViewWidth) {
                        detailed = mCourse.professor_name
                                + "\n" + schoolName
                                + "\n" + String.format(getString(R.string.s_students), studentsCount);
                    } else {
                        detailed = mCourse.professor_name
                                + "\n" + schoolName
                                + " | " + String.format(getString(R.string.s_students), studentsCount);
                    }
                }

                TextView courseDetail = (TextView) header.findViewById(R.id.course_detail);
                courseDetail.setText(detailed);
            }
        });
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BTTable.getPostsOfCourse(mCourse.id).size() == 0
                            && BTTable.MyCourseTable.get(mCourse.id) != null
                            && BTTable.MyCourseTable.get(mCourse.id).posts_count != 0) {
                        PostJsonArray postJsonArray = BTPreference.getPostsOfCourse(getActivity(), mCourse.id);
                        if (postJsonArray != null)
                            for (PostJson post : postJsonArray.posts)
                                BTTable.PostTable.append(post.id, post);
                    }
                    mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfCourse(mCourse.id)));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clicker_bt:
                showClicker();
                break;
            case R.id.attendance_bt:
                startAttendance();
                break;
            case R.id.notice_bt:
                showNotice();
                break;
        }
    }

    /**
     * Private Methods
     */
    private void showClicker() {
        ClickerStartFragment frag = new ClickerStartFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourse.id);
        bundle.putBoolean(BTKey.EXTRA_FOR_PROFILE, false);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void startAttendance() {
        AttendanceStartFragment frag = new AttendanceStartFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourse.id);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showNotice() {
        NoticePostFragment frag = new NoticePostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mCourse.id);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void openCourse() {
        BTDialogFragment.DialogType type = BTDialogFragment.DialogType.CONFIRM;
        String title = getString(R.string.open_course);
        String message = String.format(getString(R.string.do_you_really_wish_to_open), mCourse.name);
        BTDialogFragment.OnDialogListener listener = new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {

                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.opening_course)));
                getBTService().openCourse(mCourse.id, new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        BTEventBus.getInstance().post(new UpdateCourseListEvent());
                        BTEventBus.getInstance().post(new UpdateFeedEvent());
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                    }
                });
            }

            @Override
            public void onCanceled() {
            }
        };
        BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, listener));
    }

    private void unjoinCourse() {
        BTDialogFragment.DialogType type = BTDialogFragment.DialogType.CONFIRM;
        String title = getString(R.string.unjoin_course);
        String message = String.format(getString(R.string.do_you_really_wish_to_unjoin), mCourse.name);
        BTDialogFragment.OnDialogListener listener = new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {

                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.unjoining_course)));
                getBTService().dettendCourse(mCourse.id, new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        BTEventBus.getInstance().post(new UpdateCourseListEvent());
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                    }
                });
            }

            @Override
            public void onCanceled() {
            }
        };
        BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, listener));
    }
}
