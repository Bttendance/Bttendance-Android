package com.bttendance.activity.guide;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.adapter.GuidePagerAdapter;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTPreference;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 08. 14..
 */
public class IntroductionActivity extends BTActivity implements ViewPager.OnPageChangeListener {

    GuidePagerAdapter mPagerAdapter;
    Boolean mAuth;

    @InjectView(R.id.viewpager)
    ViewPager mPagerView;
    @InjectView(R.id.page_indicator)
    CirclePageIndicator mCircleIndicator;
    @InjectView(R.id.introduction_first)
    ImageView mGuideFirst;
    @InjectView(R.id.introduction_clicker)
    ImageView mGuideClicker;
    @InjectView(R.id.introduction_attendance)
    ImageView mGuideAttendance;
    @InjectView(R.id.introduction_notice)
    ImageView mGuideNotice;
    @InjectView(R.id.introduction_last)
    ImageView mGuideLast;
    @InjectView(R.id.next)
    Button mNextBt;
    @InjectView(R.id.close)
    Button mCloseBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = BTPreference.hasAuth(this);

        setContentView(R.layout.activity_introduction);
        ButterKnife.inject(this);

        mPagerAdapter = new GuidePagerAdapter(getSupportFragmentManager());
        mPagerView.setAdapter(mPagerAdapter);

        mCircleIndicator.setViewPager(mPagerView);
        mCircleIndicator.setOnPageChangeListener(this);

        mCircleIndicator.setStrokeColor(getResources().getColor(android.R.color.transparent));
        mCircleIndicator.setStrokeWidth(0);
        mCircleIndicator.setRadius(DipPixelHelper.getPixel(this, 4));

        mNextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPagerView.setCurrentItem(mPagerView.getCurrentItem() + 1, true);
            }
        });

        mCloseBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(mAuth)
            mCloseBt.setText(getText(R.string.close));
        else
            mCloseBt.setText(getText(R.string.skip));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 11) {
            mGuideFirst.setImageResource(R.drawable.welcome_bg);
            mGuideClicker.setImageResource(R.drawable.clicker_bg);
            mGuideAttendance.setImageResource(R.drawable.attendance_bg);
            mGuideNotice.setImageResource(R.drawable.notice_bg);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (Build.VERSION.SDK_INT >= 11) {

            float scrollOffset = position + positionOffset;

            if (scrollOffset < 0) {
                mGuideFirst.setAlpha(1.0f);
                mGuideClicker.setAlpha(0.0f);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (0 <= scrollOffset && scrollOffset < 1) {
                mGuideFirst.setAlpha(1.0f - scrollOffset);
                mGuideClicker.setAlpha(scrollOffset);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (1 <= scrollOffset && scrollOffset < 2) {
                mGuideFirst.setAlpha(0.0f);
                mGuideClicker.setAlpha(2.0f - scrollOffset);
                mGuideAttendance.setAlpha(scrollOffset - 1.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (2 <= scrollOffset && scrollOffset < 3) {
                mGuideFirst.setAlpha(0.0f);
                mGuideClicker.setAlpha(0.0f);
                mGuideAttendance.setAlpha(3.0f - scrollOffset);
                mGuideNotice.setAlpha(scrollOffset - 2.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (3 <= scrollOffset && scrollOffset < 4) {
                mGuideFirst.setAlpha(0.0f);
                mGuideClicker.setAlpha(0.0f);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(4.0f - scrollOffset);
                mGuideLast.setAlpha(scrollOffset - 3.0f);
                mNextBt.setAlpha(4.0f - scrollOffset);
            }

            if (4 <= scrollOffset) {
                mGuideFirst.setAlpha(0.0f);
                mGuideClicker.setAlpha(0.0f);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(1.0f);
                mNextBt.setAlpha(0.0f);
            }

            if (scrollOffset < 3.5) {
                mCloseBt.setTextColor(getResources().getColor(R.color.bttendance_white));
                mCircleIndicator.setPageColor(getResources().getColor(R.color.bttendance_white_50));
                mCircleIndicator.setFillColor(getResources().getColor(R.color.bttendance_white_80));
            } else {
                mCloseBt.setTextColor(getResources().getColor(R.color.bttendance_black));
                mCircleIndicator.setPageColor(getResources().getColor(R.color.bttendance_silver_50));
                mCircleIndicator.setFillColor(getResources().getColor(R.color.bttendance_silver_80));
            }
        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
