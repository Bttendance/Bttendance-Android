package com.bttendance.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bttendance.BuildConfig;
import com.bttendance.R;
import com.bttendance.activity.course.AttendCourseActivity;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.activity.guide.IntroductionActivity;
import com.bttendance.adapter.SideListAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.course.CourseDetailFragment;
import com.bttendance.fragment.course.NoCourseFragment;
import com.bttendance.fragment.profile.ProfileFragment;
import com.bttendance.fragment.setting.SettingFragment;
import com.bttendance.helper.ScreenHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BTDialog;
import com.bttendance.view.BTToast;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class MainActivity extends BTActivity implements AdapterView.OnItemClickListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private SideListAdapter mSideAdapter;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.left_drawer)
    ListView mListMenu;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.adView)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mSideAdapter = new SideListAdapter(getApplicationContext());
        mSideAdapter.setNotifyOnChange(true);
        mListMenu.setAdapter(mSideAdapter);
        mListMenu.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,               /* Toolbar object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                supportInvalidateOptionsMenu();
                replacePendingFragment();
                loadAd();
//                if (getBTService() != null)
//                    getBTService().socketConnect();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
                refreshSideMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setHomeAsUpIndicator(getV7DrawerToggleDelegate().getThemeUpIndicator());
        setSupportActionBar(toolbar);

        ViewGroup.LayoutParams params = mListMenu.getLayoutParams();
        params.width = ScreenHelper.getNaviDrawerWidth(this);
        mListMenu.setLayoutParams(params);

        BTEventBus.getInstance().register(mEventDispatcher);

        // Back Button Show for added Fragment
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                } else {
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                }
            }
        });

        setFirstFragment();
    }

    private void setFirstFragment() {
        BTFragment fragment;
        int lastCourse = BTPreference.getLastSeenCourse(this);
        if (lastCourse == Integer.MIN_VALUE)
            fragment = new NoCourseFragment();
        else {
            fragment = new CourseDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(BTKey.EXTRA_COURSE_ID, lastCourse);
            fragment.setArguments(bundle);
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            fm.beginTransaction().replace(R.id.content, fragment).commit();
            fragment.onPendingFragmentResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAd();

//        if (BTTable.getAttdCheckingIds().size() > 0)
//            BTEventBus.getInstance().post(new AttdStartedEvent(true));
    }

    @Override
    protected void onServieConnected() {
        super.onServieConnected();
        getBTService().autoSignIn(null);
        if (getIntent() != null && BTKey.IntentKey.ACTION_SHOW_COURSE.equals(getIntent().getAction())) {
            String courseID = getIntent().getStringExtra(BTKey.EXTRA_COURSE_ID);
            setResetCourseID(Integer.parseInt(courseID));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mListMenu))
                    mDrawerLayout.closeDrawer(mListMenu);
                else if (fm.getBackStackEntryCount() > 0)
                    super.onBackPressed();
                else
                    mDrawerLayout.openDrawer(mListMenu);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ************************
     * Option Menu
     * *************************
     */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        if (isDrawerVisible()) {
            actionBar.setTitle(getString(R.string.app_name_capital));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.no_menu, menu);
        }

        syncToggleState();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean isDrawerVisible() {
        return mDrawerLayout.isDrawerVisible(mListMenu);
    }

    public void syncToggleState() {
        mDrawerToggle.syncState();
    }

    /**
     * ************************
     * Back Press
     * *************************
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mListMenu))
            mDrawerLayout.closeDrawer(mListMenu);
        else
            super.onBackPressed();
    }

    /**
     * ************************
     * onItemClick
     * *************************
     */

    private BTFragment pendingFragment;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        SideListAdapter.SideItem item = mSideAdapter.getItem(position);

        BTFragment fragment = null;

        switch (item.getType()) {
            case Header:
                fragment = new ProfileFragment();
                break;
            case AddCourse: {
                showAddCourse();
                break;
            }
            case Guide:
                showIntroduction();
                break;
            case Setting:
                fragment = new SettingFragment();
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                break;
            case Feedback:
                UserJson user = BTTable.getMe();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@bttendance.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.feedback_subject), user.name));
                i.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.feedback_body), user.email, user.name, Build.DEVICE, BuildConfig.VERSION_NAME, user.name));
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.feedback_bttendance)));
                } catch (android.content.ActivityNotFoundException ex) {
                    BTToast.show(this, getString(R.string.feedback_error_toast_message));
                }
                break;
            case Course:
//                fragment = new CourseDetailFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt(BTKey.EXTRA_COURSE_ID, (Integer) item.getObject());
//                fragment.setArguments(bundle);
                break;
            case Section: //nothing happens
            case Margin:
            default:
                break;
        }

        if (fragment != null) {
            pendingFragment = fragment;
            mDrawerLayout.closeDrawer(mListMenu);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetMainFragment();
    }

    private int mResetCourseID = 0;

    public void setResetCourseID(int courseID) {
        mResetCourseID = courseID;

        if (isVisible())
            resetMainFragment();
    }

    private void resetMainFragment() {
        if (mResetCourseID == 0)
            return;

        BTFragment frag = (BTFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (frag instanceof CourseDetailFragment && ((CourseDetailFragment) frag).getCourseID() == mResetCourseID)
            return;

        CourseDetailFragment fragment = new CourseDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BTKey.EXTRA_COURSE_ID, mResetCourseID);
        fragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 0) {
            onBackPressed();
        }

        fm.beginTransaction().replace(R.id.content, fragment).commit();
        fragment.onPendingFragmentResume();

        mDrawerLayout.closeDrawer(mListMenu);
        mResetCourseID = 0;
    }

    public void refreshSideList() {
        mSideAdapter.refreshAdapter();
    }

    public void showAddCourse() {
        BTDialog.context(this,
                getString(R.string.add_course),
                new String[]{getString(R.string.create_course_instructor), getString(R.string.attend_course_student)},
                new BTDialog.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        if (getString(R.string.create_course_instructor).equals(edit)) {
                            Intent intent = new Intent(MainActivity.this, CreateCourseActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
                        }

                        if (getString(R.string.attend_course_student).equals(edit)) {
                            Intent intent = new Intent(MainActivity.this, AttendCourseActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
                        }
                    }

                    @Override
                    public void onCanceled() {
                    }
                });
    }

    public void showIntroduction() {
        Intent intent = new Intent(this, IntroductionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // onDrawerClosed
    private void replacePendingFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (pendingFragment != null && fm.getBackStackEntryCount() == 0) {
            if (pendingFragment instanceof SettingFragment || pendingFragment instanceof ProfileFragment)
                BTEventBus.getInstance().post(new AddFragmentEvent(pendingFragment));
            else
                fm.beginTransaction().replace(R.id.content, pendingFragment).commit();
        }
        pendingFragment = null;
    }

    // onDrawerOpened
    private void refreshSideMenu() {
        if (getBTService() == null)
            return;

        getBTService().getMyCourses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courseJsons, Response response) {
                if (mSideAdapter != null)
                    mSideAdapter.refreshAdapter();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    protected void loadAd() {
        try {
            mAdView.loadAd(new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("BE5D7D1E701EF21AB93369A353CAA3ED")
                    .addTestDevice("A642C45F5DD4C0E09AA896DDABD36789")
                    .build());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
