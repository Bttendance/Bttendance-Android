package com.bttendance.event;

import com.bttendance.fragment.BTDialogFragment;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class ShowAlertDialogEvent {

    private BTDialogFragment.DialogType mType;
    private String mTitle;
    private String mMessage;
    private String mPlaceholder;
    private BTDialogFragment.OnConfirmListener mListener;

    public ShowAlertDialogEvent(BTDialogFragment.DialogType type, String title, String message) {
        this(type, title, message, null);
    }

    public ShowAlertDialogEvent(BTDialogFragment.DialogType type, String title, String message, BTDialogFragment.OnConfirmListener listener) {
        this(type, title, message, null, listener);
    }

    public ShowAlertDialogEvent(BTDialogFragment.DialogType type, String title, String message, String placeholder, BTDialogFragment.OnConfirmListener listener) {
        mType = type;
        mTitle = title;
        mMessage = message;
        mPlaceholder = placeholder;
        mListener = listener;
    }

    public BTDialogFragment.DialogType getType() {
        return mType;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getPlaceholder() {
        return mPlaceholder;
    }

    public BTDialogFragment.OnConfirmListener getListener() {
        return mListener;
    }
}
