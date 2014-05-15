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
import com.bttendance.event.attendance.AttdStartEvent;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.event.update.UpdateFeedEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.PostCursor;
import com.bttendance.model.json.CourseJsonHelper;
import com.bttendance.model.json.EmailJson;
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
    boolean mAuth;

    public CourseDetailFragment(int courseID) {
        UserJson user = BTPreference.getUser(getActivity());
        mCourseHelper = new CourseJsonHelper(user, courseID);
        mAuth = IntArrayHelper.contains(user.supervising_courses, mCourseHelper.getID());
    }

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(mCourseHelper.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_setting:
                if (mAuth) {
                    String[] options = {getString(R.string.show_grades), getString(R.string.export_grades), getString(R.string.add_manager)};
                    BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                        @Override
                        public void onConfirmed(String edit) {
                            if (getString(R.string.show_grades).equals(edit))
                                showGrade();
                            if (getString(R.string.export_grades).equals(edit))
                                exportGrade();
                            if (getString(R.string.add_manager).equals(edit))
                                showAddManager();
                        }

                        @Override
                        public void onCanceled() {
                        }
                    }));
                } else {
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

        getBTService().courseFeed(mCourseHelper.getID(), 0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                mAdapter.swapCursor(new PostCursor(BTTable.getPostsOfCourse(mCourseHelper.getID())));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void refreshHeader() {
        if (!this.isAdded() || header == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mAuth)
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
        StartClickerFragment frag = new StartClickerFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void startAttendance() {
        BTEventBus.getInstance().post(new AttdStartEvent(mCourseHelper.getID()));
    }

    private void showNotice() {
        CreateNoticeFragment frag = new CreateNoticeFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showGrade() {
        GradeFragment frag = new GradeFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void exportGrade() {
        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.exporting_grades)));
        getBTService().courseExportGrades(mCourseHelper.getID(), new Callback<EmailJson>() {
            @Override
            public void success(EmailJson email, Response response) {
                BTDialogFragment.DialogType type = BTDialogFragment.DialogType.OK;
                String title = getString(R.string.export_grades);
                String message = String.format(getString(R.string.exporting_grade_has_been_finished), email.email);
                BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message));
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }
        });
    }

    private void showAddManager() {
        AddManagerFragment frag = new AddManagerFragment(mCourseHelper.getID());
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void unjoinCourse() {
        BTDialogFragment.DialogType type = BTDialogFragment.DialogType.CONFIRM;
        String title = getString(R.string.unjoin_course);
        String message = String.format(getString(R.string.do_you_really_wish_to), mCourseHelper.getName());
        BTDialogFragment.OnDialogListener listener = new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {

                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.unjoining_course)));
                getBTService().dettendCourse(mCourseHelper.getID(), new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                        BTEventBus.getInstance().post(new UpdateCourseListEvent());
                        BTEventBus.getInstance().post(new UpdateFeedEvent());
                        getActivity().getSupportFragmentManager().popBackStack();
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
