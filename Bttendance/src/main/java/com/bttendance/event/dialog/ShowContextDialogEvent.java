package com.bttendance.event.dialog;

import com.bttendance.fragment.BTDialogFragment;

/**
 * Created by TheFinestArtist on 2014. 5. 13..
 */
public class ShowContextDialogEvent {

    private String[] mOptions;
    private BTDialogFragment.OnDialogListener mListener;

    public ShowContextDialogEvent(String[] options, BTDialogFragment.OnDialogListener listener) {
        mOptions = options;
        mListener = listener;
    }

    public String[] getOptions() {
        return mOptions;
    }

    public BTDialogFragment.OnDialogListener getListener() {
        return mListener;
    }

}
