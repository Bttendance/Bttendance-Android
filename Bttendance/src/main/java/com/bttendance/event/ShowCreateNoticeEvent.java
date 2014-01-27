package com.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowCreateNoticeEvent {

    private int mCourseId;

    public ShowCreateNoticeEvent(int courseId) {
        mCourseId = courseId;
    }

    public int getCourseId() {
        return mCourseId;
    }

}
