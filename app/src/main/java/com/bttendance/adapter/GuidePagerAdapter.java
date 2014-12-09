package com.bttendance.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.introduction.IntroductionAttendanceFragment;
import com.bttendance.fragment.introduction.IntroductionClickerFragment;
import com.bttendance.fragment.introduction.IntroductionCuriousFragment;
import com.bttendance.fragment.introduction.IntroductionFirstFragment;
import com.bttendance.fragment.introduction.IntroductionLastFragment;
import com.bttendance.fragment.introduction.IntroductionNoticeFragment;
import com.bttendance.fragment.introduction.IntroductionStartFragment;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class GuidePagerAdapter extends FragmentPagerAdapter {

    private BTFragment[] mFragments;

    public GuidePagerAdapter(FragmentManager fm, boolean hasAuth) {
        super(fm);
        mFragments = new BTFragment[6];
        mFragments[0] = new IntroductionFirstFragment();
        mFragments[1] = new IntroductionClickerFragment();
        mFragments[2] = new IntroductionAttendanceFragment();
        mFragments[3] = new IntroductionCuriousFragment();
        mFragments[4] = new IntroductionNoticeFragment();
        if (!hasAuth)
            mFragments[5] = new IntroductionStartFragment();
        else
            mFragments[5] = new IntroductionLastFragment();
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
