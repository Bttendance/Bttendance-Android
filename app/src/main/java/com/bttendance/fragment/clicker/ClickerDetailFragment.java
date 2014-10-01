package com.bttendance.fragment.clicker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

    @InjectView(R.id.clicker)
    RelativeLayout mClicker;

    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.message)
    TextView mMessage;

    @InjectView(R.id.a_layout)
    View mALayout;
    @InjectView(R.id.a_percent)
    TextView mAPercentTv;
    @InjectView(R.id.a_line)
    View mALine;
    @InjectView(R.id.a_student)
    TextView mAStudentTv;

    @InjectView(R.id.b_layout)
    View mBLayout;
    @InjectView(R.id.b_percent)
    TextView mBPercentTv;
    @InjectView(R.id.b_line)
    View mBLine;
    @InjectView(R.id.b_student)
    TextView mBStudentTv;

    @InjectView(R.id.c_layout)
    View mCLayout;
    @InjectView(R.id.c_percent)
    TextView mCPercentTv;
    @InjectView(R.id.c_line)
    View mCLine;
    @InjectView(R.id.c_student)
    TextView mCStudentTv;

    @InjectView(R.id.d_layout)
    View mDLayout;
    @InjectView(R.id.d_percent)
    TextView mDPercentTv;
    @InjectView(R.id.d_line)
    View mDLine;
    @InjectView(R.id.d_student)
    TextView mDStudentTv;

    @InjectView(R.id.e_layout)
    View mELayout;
    @InjectView(R.id.e_percent)
    TextView mEPercentTv;
    @InjectView(R.id.e_line)
    View mELine;
    @InjectView(R.id.e_student)
    TextView mEStudentTv;

    @InjectView(R.id.student_choice)
    TextView mStudentChoice;
    @InjectView(R.id.show_details_layout)
    View mShowDetail;
    @InjectView(R.id.show_details)
    View mShowDetailBt;

    @InjectView(R.id.clicker_option_guide)
    TextView mClickerOptionGuide;

    String message = null;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (mPost == null || mPost.clicker == null)
                return;

            int progressDuration = (mPost.clicker.progress_time + 5) * 1000;
            long leftTime = progressDuration - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt);
            if (leftTime > progressDuration - 5000)
                leftTime = progressDuration - 5000;

            if (leftTime < 0) {
                timerHandler.removeCallbacks(timerRunnable);
                reDrawView();
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
        ButterKnife.inject(this, view);

        mShowDetailBt.setOnClickListener(new View.OnClickListener() {
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

        String guideText;
        if ("all".equals(mPost.clicker.detail_privacy))
            guideText = getString(R.string.clicker_guide_detail_privacy_all);
        else if ("none".equals(mPost.clicker.detail_privacy))
            guideText = getString(R.string.clicker_guide_detail_privacy_none);
        else
            guideText = getString(R.string.clicker_guide_detail_privacy_professor);

        if (mPost.clicker.show_info_on_select)
            guideText = guideText + "<br>" + getString(R.string.clicker_guide_show_info_on_select_true);
        else
            guideText = guideText + "<br>" + getString(R.string.clicker_guide_show_info_on_select_false);

        guideText = guideText + "<br>" + String.format(getString(R.string.clicker_guide_progress_time), mPost.clicker.progress_time / 60);
        mClickerOptionGuide.setText(Html.fromHtml(guideText));

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

                if ((mPost.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0) {
                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler.postDelayed(timerRunnable, 0);
                } else {
                    timerHandler.removeCallbacks(timerRunnable);
                    mMessage.setText(message);
                }

                mClicker.removeAllViews();
                int pix_280 = (int) DipPixelHelper.getPixel(getActivity(), 280);
                DefaultRenderer renderer = mPost.clicker.getRenderer(getActivity());
                GraphicalView chartView;
                if (!mAuth && !mPost.clicker.show_info_on_select && (mPost.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0)
                    chartView = ChartFactory.getPieChartView(getActivity(), mPost.clicker.getEmptySeries(), renderer);
                else
                    chartView = ChartFactory.getPieChartView(getActivity(), mPost.clicker.getSeries(), renderer);
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

                if (!mAuth && !mPost.clicker.show_info_on_select && (mPost.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0) {
                    mAPercentTv.setText("" + 0);
                    mBPercentTv.setText("" + 0);
                    mCPercentTv.setText("" + 0);
                    mDPercentTv.setText("" + 0);
                    mEPercentTv.setText("" + 0);

                    mAStudentTv.setText("" + 0);
                    mBStudentTv.setText("" + 0);
                    mCStudentTv.setText("" + 0);
                    mDStudentTv.setText("" + 0);
                    mEStudentTv.setText("" + 0);
                } else {
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
                }

                if (mAuth) {
                    mStudentChoice.setVisibility(View.GONE);
                } else {
                    if (mPost.clicker.getChoice(mUser.id) == null)
                        mStudentChoice.setText(getString(R.string.clicker_student_no_choice));
                    else
                        mStudentChoice.setText(String.format(getString(R.string.clicker_student_choice), mPost.clicker.getChoice(mUser.id)));
                    mStudentChoice.setVisibility(View.VISIBLE);
                }

                if ("all".equals(mPost.clicker.detail_privacy)
                        || ("professor".equals(mPost.clicker.detail_privacy) && mAuth)) {
                    mShowDetail.setVisibility(View.VISIBLE);
                } else {
                    mShowDetail.setVisibility(View.GONE);
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
        if (getActivity() == null || mPost == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
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
                            ClickerCRUDFragment fragment = new ClickerCRUDFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(BTKey.EXTRA_TYPE, ClickerCRUDFragment.ClickerType.CLICKER_EDIT);
                            bundle.putInt(BTKey.EXTRA_POST_ID, mPost.id);
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
