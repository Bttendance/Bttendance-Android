package com.bttendance.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.bttendance.BTDebug;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.MainActivity;
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
        BTDebug.LogError("onFragmentResume : " + ((Object) this).getClass().getSimpleName());
    }

    public void onFragmentPause() {
    }

    @Override
    public void onServiceConnected() {
        if (mPendingResume) {
            onFragmentResume();
            mPendingResume = false;
        }
    }

    @Override
    public void onServiceDisconnected() {
    }

    protected void syncToogleState() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            ((MainActivity) activity).syncToggleState();
    }
}
