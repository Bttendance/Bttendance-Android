package com.bttendance.event;

/**
 * Let Student know that Attendance check has been started.
 * So get some data from server!
 *
 * Created by TheFinestArtist on 2013. 12. 25..
 */
public class AttdStartedEvent {

    private boolean mOnGoingCheck;

    public AttdStartedEvent(boolean onGoingCheck) {
        mOnGoingCheck = onGoingCheck;
    }

    public boolean onGoingCheck() {
        return mOnGoingCheck;
    }
}
