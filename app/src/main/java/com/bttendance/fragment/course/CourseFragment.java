package com.bttendance.fragment.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.feature.home.CourseHomeFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.view.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2015. 01. 08..
 */
public class CourseFragment extends BTFragment {

    int courseId;
    FragmentPagerAdapter pagerAdapter;

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseId = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        BTPreference.setLastSeenCourse(getActivity(), courseId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        ButterKnife.inject(this, view);
        pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), courseId);
        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);
        tabs.setIconTabSelected(0);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        CourseJson course = BTTable.MyCourseTable.get(courseId);
        actionBar.setTitle(course != null ? course.name : null);
    }

    public static class PagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        private final int[] ICONS = {
                R.drawable.tabs_home,
                R.drawable.tabs_attendance,
                R.drawable.tabs_clicker,
                R.drawable.tabs_notice,
                R.drawable.tabs_curious};

        private int courseId;

        public PagerAdapter(FragmentManager fragmentManager, int courseId) {
            super(fragmentManager);
            this.courseId = courseId;
        }

        @Override
        public int getCount() {
            return ICONS.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new CourseHomeFragment();
                    break;
                case 1:
                    fragment = new NoCourseFragment();
                    break;
                case 2:
                    fragment = new NoCourseFragment();
                    break;
                case 3:
                    fragment = new NoCourseFragment();
                    break;
                case 4:
                default:
                    fragment = new NoCourseFragment();
                    break;
            }

            Bundle bundle = new Bundle();
            bundle.putInt(BTKey.EXTRA_COURSE_ID, courseId);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + position;
        }

        @Override
        public int getPageIconResId(int position) {
            return ICONS[position];
        }
    }

    public int getCourseId() {
        return courseId;
    }
}
