package com.bttendance.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.guide.GuideAttendanceFragment;
import com.bttendance.fragment.guide.GuideFirstFragment;
import com.bttendance.fragment.guide.GuideLastFragment;
import com.bttendance.fragment.guide.GuideNoticeFragment;
import com.bttendance.fragment.guide.GuidePollFragment;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class GuidePagerAdapter extends FragmentPagerAdapter {

    private BTFragment[] mFragments;

    public GuidePagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new BTFragment[5];
        mFragments[0] = new GuideFirstFragment();
        mFragments[1] = new GuidePollFragment();
        mFragments[2] = new GuideAttendanceFragment();
        mFragments[3] = new GuideNoticeFragment();
        mFragments[4] = new GuideLastFragment();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments[i];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
