package com.utopia.bttendance.fragment;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.service.BTService;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTFragment extends SherlockFragment {

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
}
