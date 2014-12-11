package com.bttendance.event;

/**
 * Created by TheFinestArtist on 2014. 12. 11..
 */
public class ShowToastEvent {

    private String mMessage;

    public ShowToastEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
