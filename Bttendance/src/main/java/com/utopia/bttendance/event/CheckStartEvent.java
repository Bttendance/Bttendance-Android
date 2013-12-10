package com.utopia.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 8..
 */
public class CheckStartEvent {

    private int mCourseId;

    public CheckStartEvent(int course_id) {
        mCourseId = course_id;
    }

    public int getCourseId() {
        return mCourseId;
    }
}
