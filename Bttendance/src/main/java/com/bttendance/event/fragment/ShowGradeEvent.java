package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowGradeEvent {

    private int mCourseId;

    public ShowGradeEvent(int courseId) {
        mCourseId = courseId;
    }

    public int getCourseId() {
        return mCourseId;
    }

}
