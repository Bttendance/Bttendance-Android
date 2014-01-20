package com.bttendance.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.squareup.otto.BTEventBus;
import com.bttendance.R;
import com.bttendance.event.AttdStartedEvent;
import com.bttendance.fragment.CourseListFragment;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BeautiToast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class ProfessorActivity extends BTActivity {

    private static Handler mUIHandler = new Handler();
    private boolean mFinishApplication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_professor);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CourseListFragment frag = new CourseListFragment();
        ft.add(R.id.content, frag, ((Object) frag).getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
        return super.onCreateOptionsMenu(menu);
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

        //Check whether on going Attendance exists
        getBTService().courses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courseJsons, Response response) {
                if (BTTable.getCheckingPostIds().size() > 0)
                    BTEventBus.getInstance().post(new AttdStartedEvent(true));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            invalidateOptionsMenu();
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