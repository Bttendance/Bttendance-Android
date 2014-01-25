package com.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowPostAttdEvent {

    private int mCourseId;

    public ShowPostAttdEvent(int courseId) {
        mCourseId = courseId;
    }

    public int getCourseId() {
        return mCourseId;
    }

}
