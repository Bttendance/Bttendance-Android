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
    ImageView mIntroductionFirst;
    @InjectView(R.id.introduction_clicker)
    ImageView mIntroductionClicker;
    @InjectView(R.id.introduction_attendance)
    ImageView mIntroductionAttendance;
    @InjectView(R.id.introduction_curious)
    ImageView mIntrodcutionCurious;
    @InjectView(R.id.introduction_notice)
    ImageView mIntroductionNotice;
    @InjectView(R.id.introduction_last)
    ImageView mIntroductionLast;
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

        if (mAuth)
            mCloseBt.setText(getText(R.string.close));
        else
            mCloseBt.setText(getText(R.string.skip));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 11) {
            mIntroductionFirst.setImageResource(R.drawable.welcome_bg);
            mIntroductionClicker.setImageResource(R.drawable.clicker_bg);
            mIntroductionAttendance.setImageResource(R.drawable.attendance_bg);
            mIntrodcutionCurious.setImageResource(R.drawable.clicker_bg);
            mIntroductionNotice.setImageResource(R.drawable.notice_bg);
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
                mIntroductionFirst.setAlpha(1.0f);
                mIntroductionClicker.setAlpha(0.0f);
                mIntroductionAttendance.setAlpha(0.0f);
                mIntrodcutionCurious.setAlpha(0.0f);
                mIntroductionNotice.setAlpha(0.0f);
                mIntroductionLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (0 <= scrollOffset && scrollOffset < 1) {
                mIntroductionFirst.setAlpha(1.0f - scrollOffset);
                mIntroductionClicker.setAlpha(scrollOffset);
                mIntroductionAttendance.setAlpha(0.0f);
                mIntrodcutionCurious.setAlpha(0.0f);
                mIntroductionNotice.setAlpha(0.0f);
                mIntroductionLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (1 <= scrollOffset && scrollOffset < 2) {
                mIntroductionFirst.setAlpha(0.0f);
                mIntroductionClicker.setAlpha(2.0f - scrollOffset);
                mIntroductionAttendance.setAlpha(scrollOffset - 1.0f);
                mIntrodcutionCurious.setAlpha(0.0f);
                mIntroductionNotice.setAlpha(0.0f);
                mIntroductionLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (2 <= scrollOffset && scrollOffset < 3) {
                mIntroductionFirst.setAlpha(0.0f);
                mIntroductionClicker.setAlpha(0.0f);
                mIntroductionAttendance.setAlpha(3.0f - scrollOffset);
                mIntrodcutionCurious.setAlpha(scrollOffset - 2.0f);
                mIntroductionNotice.setAlpha(0.0f);
                mIntroductionLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (3 <= scrollOffset && scrollOffset < 4) {
                mIntroductionFirst.setAlpha(0.0f);
                mIntroductionClicker.setAlpha(0.0f);
                mIntroductionAttendance.setAlpha(0.0f);
                mIntrodcutionCurious.setAlpha(4.0f - scrollOffset);
                mIntroductionNotice.setAlpha(scrollOffset - 3.0f);
                mIntroductionLast.setAlpha(0.0f);
                mNextBt.setAlpha(1.0f);
            }

            if (4 <= scrollOffset && scrollOffset < 5) {
                mIntroductionFirst.setAlpha(0.0f);
                mIntroductionClicker.setAlpha(0.0f);
                mIntroductionAttendance.setAlpha(0.0f);
                mIntrodcutionCurious.setAlpha(0.0f);
                mIntroductionNotice.setAlpha(5.0f - scrollOffset);
                mIntroductionLast.setAlpha(scrollOffset - 4.0f);
                mNextBt.setAlpha(4.0f - scrollOffset);
            }

            if (5 <= scrollOffset) {
                mIntroductionFirst.setAlpha(0.0f);
                mIntroductionClicker.setAlpha(0.0f);
                mIntroductionAttendance.setAlpha(0.0f);
                mIntrodcutionCurious.setAlpha(0.0f);
                mIntroductionNotice.setAlpha(0.0f);
                mIntroductionLast.setAlpha(1.0f);
                mNextBt.setAlpha(0.0f);
            }

            if (scrollOffset < 4.5) {
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
