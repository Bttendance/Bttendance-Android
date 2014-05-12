package com.bttendance.event;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class ShowProgressDialogEvent {

    private String mMessage;
    private boolean mShow;

    public ShowProgressDialogEvent(String message, boolean show) {
        mMessage = message;
        mShow = show;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean getShow() {
        return mShow;
    }
}
