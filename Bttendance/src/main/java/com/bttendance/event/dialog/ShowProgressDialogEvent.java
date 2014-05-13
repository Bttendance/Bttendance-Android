package com.bttendance.event.dialog;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class ShowProgressDialogEvent {

    private String mMessage;

    public ShowProgressDialogEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

}
