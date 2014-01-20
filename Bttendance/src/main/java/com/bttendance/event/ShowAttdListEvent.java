package com.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowAttdListEvent {

    private int mCourseId;

    public ShowAttdListEvent(int courseId) {
        mCourseId = courseId;
    }

    public int getCourseId() {
        return mCourseId;
    }

}
