package com.bttendance.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bttendance.R;
import com.bttendance.adapter.GuidePagerAdapter;
import com.bttendance.helper.DipPixelHelper;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by TheFinestArtist on 2014. 08. 14..
 */
public class GuideActivity extends BTActivity implements ViewPager.OnPageChangeListener {

    GuidePagerAdapter mPagerAdapter;
    ViewPager mPagerView;
    CirclePageIndicator mCircleIndicator;
    ImageView mGuideFirst;
    ImageView mGuidePoll;
    ImageView mGuideAttendance;
    ImageView mGuideNotice;
    ImageView mGuideLast;
    Button mNextBt;
    Button mCloseBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mPagerAdapter = new GuidePagerAdapter(getSupportFragmentManager());
        mPagerView = (ViewPager) findViewById(R.id.viewpager);
        mPagerView.setAdapter(mPagerAdapter);

        mCircleIndicator = (CirclePageIndicator) findViewById(R.id.page_indicator);
        mCircleIndicator.setViewPager(mPagerView);
        mCircleIndicator.setOnPageChangeListener(this);

        mCircleIndicator.setStrokeColor(getResources().getColor(android.R.color.transparent));
        mCircleIndicator.setStrokeWidth(0);
        mCircleIndicator.setRadius(DipPixelHelper.getPixel(this, 4));

        mGuideFirst = (ImageView) findViewById(R.id.guide_first);
        mGuidePoll = (ImageView) findViewById(R.id.guide_poll);
        mGuideAttendance = (ImageView) findViewById(R.id.guide_attendance);
        mGuideNotice = (ImageView) findViewById(R.id.guide_notice);
        mGuideLast = (ImageView) findViewById(R.id.guide_last);

        mNextBt = (Button) findViewById(R.id.next);
        mCloseBt = (Button) findViewById(R.id.close);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 11) {
            mGuideFirst.setImageResource(R.drawable.welcome_bg);
            mGuidePoll.setImageResource(R.drawable.poll_bg);
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
                mGuidePoll.setAlpha(0.0f);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (0 <= scrollOffset && scrollOffset < 1) {
                mGuideFirst.setAlpha(1.0f - scrollOffset);
                mGuidePoll.setAlpha(scrollOffset);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (1 <= scrollOffset && scrollOffset < 2) {
                mGuideFirst.setAlpha(0.0f);
                mGuidePoll.setAlpha(2.0f - scrollOffset);
                mGuideAttendance.setAlpha(scrollOffset - 1.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (2 <= scrollOffset && scrollOffset < 3) {
                mGuideFirst.setAlpha(0.0f);
                mGuidePoll.setAlpha(0.0f);
                mGuideAttendance.setAlpha(3.0f - scrollOffset);
                mGuideNotice.setAlpha(scrollOffset - 2.0f);
                mGuideLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (3 <= scrollOffset && scrollOffset < 4) {
                mGuideFirst.setAlpha(0.0f);
                mGuidePoll.setAlpha(0.0f);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(4.0f - scrollOffset);
                mGuideLast.setAlpha(scrollOffset - 3.0f);
                mNextBt.setAlpha(4.0f - scrollOffset);
            }

            if (4 <= scrollOffset) {
                mGuideFirst.setAlpha(0.0f);
                mGuidePoll.setAlpha(0.0f);
                mGuideAttendance.setAlpha(0.0f);
                mGuideNotice.setAlpha(0.0f);
                mGuideLast.setAlpha(1.0f);
                mNextBt.setAlpha(0.0f);
            }

            if (scrollOffset < 3.5) {
                mCloseBt.setBackgroundResource(R.drawable.x);
                mCircleIndicator.setPageColor(getResources().getColor(R.color.bttendance_white_50));
                mCircleIndicator.setFillColor(getResources().getColor(R.color.bttendance_white_80));
            } else {
                mCloseBt.setBackgroundResource(R.drawable.x_black);
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
