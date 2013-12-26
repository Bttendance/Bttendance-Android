package com.utopia.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 25..
 */
public class AttendanceStartedEvent {
    private int mPostId;

    public AttendanceStartedEvent(int postId) {
        mPostId = postId;
    }

    public int getPostId() {
        return mPostId;
    }
}
