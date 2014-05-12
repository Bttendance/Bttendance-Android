package com.bttendance.event;

import com.bttendance.fragment.BTDialogFragment;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class ShowDialogEvent {

    private BTDialogFragment mDialog;
    private String mName;

    public ShowDialogEvent(BTDialogFragment dialog, String name) {
        mDialog = dialog;
        mName = name;
    }

    public BTDialogFragment getDialog() {
        return mDialog;
    }

    public String getName() {
        return mName;
    }
}
