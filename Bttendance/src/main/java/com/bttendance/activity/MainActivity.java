package com.bttendance.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.bttendance.R;
import com.bttendance.adapter.SideListAdapter;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.update.UpdateCourseListEvent;
import com.bttendance.event.update.UpdateProfileEvent;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private static Handler mUIHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SideListAdapter mSideAdapter;
    private ListView mListMenu;


    private boolean mFinishApplication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityStack.clear(this);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mSideAdapter = new SideListAdapter(getApplicationContext());
        mSideAdapter.setNotifyOnChange(true);
        mListMenu = (ListView) findViewById(R.id.left_drawer);
        mListMenu.setAdapter(mSideAdapter);
        mListMenu.setOnItemClickListener(this);
//        ViewGroup.LayoutParams params = mListMenu.getLayoutParams();
//        params.width = ScreenHelper.getNaviDrawerWidth(this);
//        mListMenu.setLayoutParams(params);

//        BTEventBus.getInstance().register(mEventDispatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (BTTable.getAttdCheckingIds().size() > 0)
            BTEventBus.getInstance().post(new AttdStartedEvent(true));
    }

//    @Override
//    protected void onServieConnected() {
//        super.onServieConnected();
//
//        //Check whether on going Attendance exists
//        getBTService().autoSignin(new Callback<UserJson>() {
//            @Override
//            public void success(UserJson userJson, Response response) {
//                BTEventBus.getInstance().post(new UpdateCourseListEvent());
//                BTEventBus.getInstance().post(new UpdateProfileEvent());
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//            }
//        });
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public void onBackPressed() {
//        FragmentManager fm = getSupportFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            super.onBackPressed();
//        } else
//            tryToFinish();
//    }
//
//    private void tryToFinish() {
//        if (mFinishApplication) {
//            finish();
//        } else {
//            BeautiToast.show(this, getString(R.string.please_press_back_button_again_to_exit_));
//            mFinishApplication = true;
//            mUIHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mFinishApplication = false;
//                }
//            }, 3000);
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        SideListAdapter.SideItem item = mSideAdapter.getItem(position);
    }
}
