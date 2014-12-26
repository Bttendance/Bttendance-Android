package com.bttendance.event;

import com.bttendance.fragment.BTFragment;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class AddFragmentEvent {

    private BTFragment mFragment;
    private boolean mHasAnim;

    public AddFragmentEvent(BTFragment fragment) {
        mFragment = fragment;
        mHasAnim = true;
    }

    public AddFragmentEvent(BTFragment fragment, boolean hasAnim) {
        mFragment = fragment;
        mHasAnim = hasAnim;
    }

    public BTFragment getFragment() {
        return mFragment;
    }

    public boolean hasAnim() {
        return mHasAnim;
    }
}
