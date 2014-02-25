package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowCourseDetailEvent {

    private int mCourseId;

    public ShowCourseDetailEvent(int courseId) {
        mCourseId = courseId;
    }

    public int getCourseId() {
        return mCourseId;
    }

}
