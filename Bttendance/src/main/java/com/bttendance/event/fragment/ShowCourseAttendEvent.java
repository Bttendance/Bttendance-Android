package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowCourseAttendEvent {
    private int mSchoolID;

    public ShowCourseAttendEvent(int schoolID) {
        mSchoolID = schoolID;
    }

    public int getSchoolID() {
        return mSchoolID;
    }
}
