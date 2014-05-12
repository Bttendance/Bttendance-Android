package com.bttendance.fragment;

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
import com.bttendance.adapter.FeedAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.LoadingEvent;
import com.bttendance.event.attendance.AttdCheckedEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.event.update.UpdateFeedEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.PostCursor;
import com.bttendance.model.json.CourseJsonHelper;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
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
    CourseJsonHelper mCourseHelper;
    View header;

    public CourseDetailFragment(int courseID) {
        mCourseHelper = new CourseJsonHelper(getActivity(), courseID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        header = inflater.inflate(R.layout.course_header, null, false);
        refreshHeader();
        mListView.addHeaderView(header);
        header.findViewById(R.id.notice_bt).setOnClickListener(this);
        header.findViewById(R.id.grade_bt).setOnClickListener(this);
        header.findViewById(R.id.ta_bt).setOnClickListener(this);
        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addFooterView(padding);
        mAdapter = new FeedAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        getFeed();
    }

    public void getFeed() {
        if (getBTService() == null)
            return;

        BTEventBus.getInstance().post(new LoadingEvent(true));
        getBTService().courseFeed(mCourseHelper.getID(), 0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfCourse(mCourseHelper.getID())));
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }
        });
    }

    private void refreshHeader() {
        if (!this.isAdded() || header == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserJson user = BTPreference.getUser(getActivity());
                if (IntArrayHelper.contains(user.attending_courses, mCourseHelper.getID()))
                    header.findViewById(R.id.manager_layout).setVisibility(View.GONE);

                Bttendance bttendance = (Bttendance) header.findViewById(R.id.bttendance);
                int grade = Integer.parseInt(mCourseHelper.getGrade());
                bttendance.setBttendance(Bttendance.STATE.GRADE, grade);
                TextView courseInfo = (TextView) header.findViewById(R.id.course_info);
                courseInfo.setText(getString(R.string.prof_) + " " + mCourseHelper.getProfessorName() + "\n"
                        + mCourseHelper.getSchoolName() + "\n\n"
                        + String.format(getString(R.string.n_students), mCourseHelper.getStudentCount()) + "\n"
                        + String.format(getString(R.string.n_attendance_rate), grade) + "\n"
                        + String.format(getString(R.string.n_clickers), mCourseHelper.getClickerUsage()) + "\n"
                        + String.format(getString(R.string.n_notices), mCourseHelper.getNoticeUsage())
                );
            }
        });
    }

    @Subscribe
    public void onAttdStarted(AttdStartedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onAttdChecked(AttdCheckedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onUpdate(UpdateFeedEvent event) {
        swapCursor();
        refreshHeader();
    }

    @Subscribe
    public void onRefresh(RefreshFeedEvent event) {
        getFeed();
        refreshHeader();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapCursor();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfCourse(mCourseHelper.getID())));
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(mCourseHelper.getName());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notice_bt:
                CreateNoticeFragment createNoticeFragment = new CreateNoticeFragment(mCourseHelper.getID());
                BTEventBus.getInstance().post(new AddFragmentEvent(createNoticeFragment));
                break;
            case R.id.grade_bt:
                GradeFragment gradeFragment = new GradeFragment(mCourseHelper.getID());
                BTEventBus.getInstance().post(new AddFragmentEvent(gradeFragment));
                break;
            case R.id.ta_bt:
                AddManagerFragment addManagerFragment = new AddManagerFragment(mCourseHelper.getID());
                BTEventBus.getInstance().post(new AddFragmentEvent(addManagerFragment));
                break;
        }
    }
}
