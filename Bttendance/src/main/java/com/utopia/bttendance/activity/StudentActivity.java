package com.utopia.bttendance.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.utopia.bttendance.R;
import com.utopia.bttendance.adapter.BTPagerAdapter;
import com.utopia.bttendance.fragment.BTFragment;
import com.utopia.bttendance.helper.DipPixelHelper;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.view.BeautiToast;
import com.utopia.bttendance.view.PagerSlidingTabStrip;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class StudentActivity extends BTActivity {

    private static Handler mUIHandler = new Handler();
    BTPagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;
    private boolean mFinishApplication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_student);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new BTPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setAdapter(mPagerAdapter);

        int pageMargin = (int) DipPixelHelper.getPixel(this, 4);
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setOffscreenPageLimit(3);
        mTabs.setViewPager(mViewPager);
        mTabs.setIconTabSelected(0);
        mViewPager.setCurrentItem(0);
        getSupportActionBar().setTitle(getString(R.string.feed));

        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                updateActionBar(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void updateActionBar(int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setTitle(getString(R.string.feed));
                break;
            case 1:
                getSupportActionBar().setTitle(getString(R.string.courses));
                break;
            case 2:
                getSupportActionBar().setTitle(getString(R.string.profile));
                break;
        }
    }

    @Override
    protected void onServieConnected() {
        super.onServieConnected();
        getBTService().joinSchool(1, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            invalidateOptionsMenu();
            for (int i = 0; i < mPagerAdapter.getCount(); i++)
                if (mPagerAdapter.getItem(i) instanceof BTFragment)
                    ((BTFragment) mPagerAdapter.getItem(i)).onFragmentResume();
            super.onBackPressed();
        } else
            tryToFinish();
    }

    private void tryToFinish() {
        if (mFinishApplication) {
            finish();
        } else {
            BeautiToast.show(this, getString(R.string.please_press_back_button_again_to_exit_));
            mFinishApplication = true;
            mUIHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFinishApplication = false;
                }
            }, 3000);
        }
    }
}
