package com.utopia.bttendance.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.utopia.bttendance.R;
import com.utopia.bttendance.fragment.CourseListFragment;
import com.utopia.bttendance.view.BeautiToast;

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
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
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
