package com.utopia.bttendance.event;

/**
 * Student has been checked attendance
 * Push notification from server will notify this event
 * <p/>
 * Created by TheFinestArtist on 2013. 12. 25..
 */
public class AttdCheckedEvent {

    private String mTitle;

    public AttdCheckedEvent(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
