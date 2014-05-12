package com.bttendance.event;

import com.bttendance.fragment.BTFragment;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class AddFragmentEvent {

    private BTFragment mFragment;

    public AddFragmentEvent(BTFragment fragment) {
        mFragment = fragment;
    }

    public BTFragment getFragment() {
        return mFragment;
    }
}
