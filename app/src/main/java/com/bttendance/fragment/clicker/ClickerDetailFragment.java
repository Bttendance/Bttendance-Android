package com.bttendance.fragment.clicker;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bttendance.event.socket.ClickerUpdatedEvent;
import com.bttendance.event.socket.PostUpdatedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.feature.FeatureDetailListFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Clicker;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 30..
 */
public class ClickerDetailFragment extends BTFragment {

    private int mPostID;
    private UserJson mUser;
    private CourseJson mCourse;
    private PostJson mPost;
    private boolean mAuth;

    private RelativeLayout mClicker;

    private TextView mTitle;
    private TextView mMessage;

    private View mALayout;
    private TextView mAPercentTv;
    private View mALine;
    private TextView mAStudentTv;

    private View mBLayout;
    private TextView mBPercentTv;
    private View mBLine;
    private TextView mBStudentTv;

    private View mCLayout;
    private TextView mCPercentTv;
    private View mCLine;
    private TextView mCStudentTv;

    private View mDLayout;
    private TextView mDPercentTv;
    private View mDLine;
    private TextView mDStudentTv;

    private View mELayout;
    private TextView mEPercentTv;
    private View mELine;
    private TextView mEStudentTv;

    private TextView mStudentChoice;
    private View mShowDetail;

    String message = null;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long leftTime = Clicker.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt);
            if (leftTime > 60000)
                leftTime = 60000;

            if (leftTime < 0) {
                timerHandler.removeCallbacks(timerRunnable);
                mMessage.setText(message);
            } else {
                mMessage.setText(String.format(getString(R.string.clicker_message_left_time), message, (int) leftTime / 1000));
                timerHandler.postDelayed(this, 200);
            }
        }
    };

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
        View view = inflater.inflate(R.layout.fragment_clicker_detail, container, false);

        mTitle = (TextView) view.findViewById(R.id.title);
        mMessage = (TextView) view.findViewById(R.id.message);

        mClicker = (RelativeLayout) view.findViewById(R.id.clicker);

        mALayout = view.findViewById(R.id.a_layout);
        mAPercentTv = (TextView) view.findViewById(R.id.a_percent);
        mALine = view.findViewById(R.id.a_line);
        mAStudentTv = (TextView) view.findViewById(R.id.a_student);

        mBLayout = view.findViewById(R.id.b_layout);
        mBPercentTv = (TextView) view.findViewById(R.id.b_percent);
        mBLine = view.findViewById(R.id.b_line);
        mBStudentTv = (TextView) view.findViewById(R.id.b_student);

        mCLayout = view.findViewById(R.id.c_layout);
        mCPercentTv = (TextView) view.findViewById(R.id.c_percent);
        mCLine = view.findViewById(R.id.c_line);
        mCStudentTv = (TextView) view.findViewById(R.id.c_student);

        mDLayout = view.findViewById(R.id.d_layout);
        mDPercentTv = (TextView) view.findViewById(R.id.d_percent);
        mDLine = view.findViewById(R.id.d_line);
        mDStudentTv = (TextView) view.findViewById(R.id.d_student);

        mELayout = view.findViewById(R.id.e_layout);
        mEPercentTv = (TextView) view.findViewById(R.id.e_percent);
        mELine = view.findViewById(R.id.e_line);
        mEStudentTv = (TextView) view.findViewById(R.id.e_student);

        mStudentChoice = (TextView) view.findViewById(R.id.student_choice);

        mShowDetail = view.findViewById(R.id.show_details_layout);
        view.findViewById(R.id.show_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeatureDetailListFragment fragment = new FeatureDetailListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BTKey.EXTRA_POST_ID, mPost.id);
                bundle.putSerializable(BTKey.EXTRA_TYPE, FeatureDetailListFragment.Type.Clicker);
                fragment.setArguments(bundle);
                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
            }
        });

        return view;
    }

    @Subscribe
    public void onClickerUpdated(ClickerUpdatedEvent event) {
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

                mTitle.setText(mPost.message);
                int studentCount = 0;
                if (mCourse != null)
                    studentCount = mCourse.students_count;
                message = String.format(getString(R.string.clicker_message_normal), mPost.clicker.getParticipatedCount(), studentCount, DateHelper.getPostFormatString(mPost.createdAt));

                if (Clicker.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0) {
                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler.postDelayed(timerRunnable, 0);
                } else {
                    timerHandler.removeCallbacks(timerRunnable);
                    mMessage.setText(message);
                }

                mClicker.removeAllViews();
                int pix_280 = (int) DipPixelHelper.getPixel(getActivity(), 280);
                DefaultRenderer renderer = mPost.clicker.getRenderer(getActivity());
                CategorySeries series = mPost.clicker.getSeries();
                GraphicalView chartView = ChartFactory.getPieChartView(getActivity(), series, renderer);
                RelativeLayout.LayoutParams params_chart = new RelativeLayout.LayoutParams(pix_280, pix_280);
                params_chart.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                mClicker.addView(chartView, params_chart);

                int pix_point_7 = (int) DipPixelHelper.getPixel(getActivity(), 0.7f);
                int pix_5 = (int) DipPixelHelper.getPixel(getActivity(), 5);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pix_point_7);
                params.setMargins(0, 0, pix_5, 0);

                switch (mPost.clicker.choice_count) {
                    case 2:
                        mCLayout.setVisibility(View.GONE);
                        mBLine.setLayoutParams(params);
                    case 3:
                        mDLayout.setVisibility(View.GONE);
                        mCLine.setLayoutParams(params);
                    case 4:
                        mELayout.setVisibility(View.GONE);
                        mDLine.setLayoutParams(params);
                }

                mAPercentTv.setText("" + mPost.clicker.getPercent(1));
                mBPercentTv.setText("" + mPost.clicker.getPercent(2));
                mCPercentTv.setText("" + mPost.clicker.getPercent(3));
                mDPercentTv.setText("" + mPost.clicker.getPercent(4));
                mEPercentTv.setText("" + mPost.clicker.getPercent(5));

                mAStudentTv.setText("" + mPost.clicker.a_students.length);
                mBStudentTv.setText("" + mPost.clicker.b_students.length);
                mCStudentTv.setText("" + mPost.clicker.c_students.length);
                mDStudentTv.setText("" + mPost.clicker.d_students.length);
                mEStudentTv.setText("" + mPost.clicker.e_students.length);

                if (mAuth) {
                    mStudentChoice.setVisibility(View.GONE);
                    mShowDetail.setVisibility(View.VISIBLE);
                } else {
                    if (mPost.clicker.getChoice(mUser.id) == null)
                        mStudentChoice.setText(getString(R.string.clicker_student_no_choice));
                    else
                        mStudentChoice.setText(String.format(getString(R.string.clicker_student_choice), mPost.clicker.getChoice(mUser.id)));

                    mShowDetail.setVisibility(View.GONE);
                    mStudentChoice.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
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
        actionBar.setTitle(getString(R.string.clicker));
        if (mAuth)
            inflater.inflate(R.menu.clicker_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_post_setting:
                String[] options = {getString(R.string.edit_message), getString(R.string.delete_clicker)};
                BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        if (getString(R.string.delete_clicker).equals(edit)) {
                            if (getBTService() != null) {
                                BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.deleting_clicker)));
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

                        if (getString(R.string.edit_message).equals(edit)) {
                            ClickerStartFragment fragment = new ClickerStartFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt(BTKey.EXTRA_POST_ID, mPost.id);
                            bundle.putBoolean(BTKey.EXTRA_FOR_PROFILE, false);
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
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
