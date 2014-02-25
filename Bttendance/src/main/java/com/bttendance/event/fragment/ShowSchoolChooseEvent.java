package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowSchoolChooseEvent {
    private int mButtonID;

    public ShowSchoolChooseEvent(int buttonID) {
        mButtonID = buttonID;
    }

    public int getButtonID() {
        return mButtonID;
    }
}
