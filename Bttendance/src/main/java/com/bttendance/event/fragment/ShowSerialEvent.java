package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowSerialEvent {
    private int mSchoolID;

    public ShowSerialEvent(int schoolID) {
        mSchoolID = schoolID;
    }

    public int getSchoolID() {
        return mSchoolID;
    }
}
