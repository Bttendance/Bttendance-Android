package com.utopia.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 8..
 */
public class AttendanceStartEvent {

    private int mCourseId;

    public AttendanceStartEvent(int course_id) {
        mCourseId = course_id;
    }

    public int getCourseId() {
        return mCourseId;
    }
}
