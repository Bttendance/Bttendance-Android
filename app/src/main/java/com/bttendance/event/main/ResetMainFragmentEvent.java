package com.bttendance.event.main;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class ResetMainFragmentEvent {

    private int mCourseID;

    public ResetMainFragmentEvent(int courseID) {
        mCourseID = courseID;
    }

    public int getCourseID() {
        return mCourseID;
    }
}
