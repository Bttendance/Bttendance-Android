package com.utopia.bttendance.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.utopia.bttendance.R;
import com.utopia.bttendance.fragment.BTFragment;
import com.utopia.bttendance.fragment.CourseListFragment;
import com.utopia.bttendance.fragment.FeedFragment;
import com.utopia.bttendance.fragment.ProfileFragment;
import com.utopia.bttendance.view.PagerSlidingTabStrip;

/**
 * Created by TheFinestArtist on 13. 10. 5..
 */
public class BTPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private Context mContext;
    private FeedFragment mFeedFragment;
    private CourseListFragment mCourseListFragment;
    private ProfileFragment mProfileFragment;

    public BTPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mFeedFragment = new FeedFragment();
        mCourseListFragment = new CourseListFragment();
        mProfileFragment = new ProfileFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return mFeedFragment;
            case 1:
                return mCourseListFragment;
            case 2:
                return mProfileFragment;
            default:
                return new BTFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.feed);
            case 1:
                return mContext.getResources().getString(R.string.courses);
            case 2:
                return mContext.getResources().getString(R.string.profile);
            default:
                return "";
        }
    }

    @Override
    public int getPageIconResId(int position) {

        switch (position) {
            case 0:
                return R.drawable.ic_tab_feed;
            case 1:
                return R.drawable.ic_tab_courses;
            case 2:
                return R.drawable.ic_tab_profile;
            default:
                return R.drawable.ic_tab_courses;
        }

    }
}