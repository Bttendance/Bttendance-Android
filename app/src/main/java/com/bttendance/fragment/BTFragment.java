package com.bttendance.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.MainActivity;
import com.bttendance.activity.course.AttendCourseActivity;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.service.BTService;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTFragment extends Fragment implements BTActivity.OnServiceConnectListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null && activity instanceof BTActivity) {
            ((BTActivity) activity).addOnServiceConnectListener(this);
        }
        if (getBTService() != null)
            onServiceConnected();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView abTitle = (TextView) getActivity().findViewById(titleId);
        if (this instanceof BTDialogFragment
                && (getActivity() instanceof MainActivity
                || getActivity() instanceof AttendCourseActivity
                || getActivity() instanceof CreateCourseActivity)) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bttendance_navy_darken)));

            if (abTitle != null)
                abTitle.setTextColor(getResources().getColor(R.color.bttendance_white_darken));
        } else {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bttendance_navy)));

            if (abTitle != null)
                abTitle.setTextColor(getResources().getColor(R.color.bttendance_white));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity activity = getActivity();
        if (activity != null && activity instanceof BTActivity) {
            ((BTActivity) activity).removeOnServiceConnectListener(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BTEventBus.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BTEventBus.getInstance().unregister(this);
    }

    public BTService getBTService() {
        Activity activity = getActivity();
        if (activity instanceof BTActivity)
            return ((BTActivity) activity).getBTService();
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean mPendingResume;

    public void onPendingFragmentResume() {
        if (getBTService() == null)
            mPendingResume = true;
        else
            onFragmentResume();
    }

    public void onFragmentResume() {
    }

    public void onFragmentPause() {
    }

    @Override
    public void onServiceConnected() {
        if (mPendingResume)
            onFragmentResume();
    }

    @Override
    public void onServiceDisconnected() {
    }
}