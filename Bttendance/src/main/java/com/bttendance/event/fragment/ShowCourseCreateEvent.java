package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowCourseCreateEvent {
    private int mSchoolID;

    public ShowCourseCreateEvent(int schoolID) {
        mSchoolID = schoolID;
    }

    public int getSchoolID() {
        return mSchoolID;
    }
}
