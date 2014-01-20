package com.bttendance.event;

/**
 * Professor Clicked Bttendance Icon to check attendance
 *
 * Created by TheFinestArtist on 2013. 12. 8..
 */
public class AttdStartEvent {

    private int mCourseId;

    public AttdStartEvent(int course_id) {
        mCourseId = course_id;
    }

    public int getCourseId() {
        return mCourseId;
    }
}
