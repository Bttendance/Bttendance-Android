package com.bttendance.fragment;

import android.app.Activity;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.otto.BTEventBus;
import com.bttendance.activity.BTActivity;
import com.bttendance.service.BTService;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTFragment extends SherlockFragment implements BTActivity.OnServiceConnectListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null && activity instanceof BTActivity) {
            ((BTActivity)activity).addOnServiceConnectListener(this);
        }
        if (getBTService() != null)
            onServiceConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity activity = getActivity();
        if (activity != null && activity instanceof BTActivity) {
            ((BTActivity)activity).removeOnServiceConnectListener(this);
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

    public void onFragmentResume() {
    }

    public void onFragmentPause() {
    }

    @Override
    public void onServiceConnected() {
    }

    @Override
    public void onServiceDisconnected() {
    }
}
