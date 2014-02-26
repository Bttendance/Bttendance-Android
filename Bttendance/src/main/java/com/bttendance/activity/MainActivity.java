package com.bttendance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.bttendance.activity.sign.CatchPointActivity;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.UserJson;
import com.bttendance.R;
import com.bttendance.adapter.BTPagerAdapter;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.view.BeautiToast;
import com.bttendance.view.PagerSlidingTabStrip;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class MainActivity extends BTActivity {

    private static Handler mUIHandler = new Handler();
    BTPagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;
    private boolean mFinishApplication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_main);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new BTPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setAdapter(mPagerAdapter);

        int pageMargin = (int) DipPixelHelper.getPixel(this, 4);
        mViewPager.setPageMargin(pageMargin);
        mViewPager.setOffscreenPageLimit(3);
        mTabs.setViewPager(mViewPager);
        mTabs.setIconTabSelected(0);
        mViewPager.setCurrentItem(1);
        getSupportActionBar().setTitle(getString(R.string.courses));

        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                setActionBarTitle(i);
                fragmentResume(i);
                mPagerAdapter.setPosition(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void setActionBarTitle(int position) {
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

    private void fragmentResume(int position) {
        if (mPagerAdapter != null)
            ((BTFragment) mPagerAdapter.getItem(position)).onFragmentResume();
    }

    @Override
    protected void onServieConnected() {
        super.onServieConnected();

        //Check whether on going Attendance exists
        getBTService().autoSignin(new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTPreference.setUser(MainActivity.this, userJson);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (retrofitError != null && retrofitError.getResponse() != null && retrofitError.getResponse().getStatus() == 401) {
                    BTPreference.clearUser(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, CatchPointActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        setActionBarTitle(mPagerAdapter.getPosition());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else if (fm.getBackStackEntryCount() == 1) {
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
