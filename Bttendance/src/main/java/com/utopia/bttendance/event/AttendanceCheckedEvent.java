package com.utopia.bttendance.event;

/**
 * Created by TheFinestArtist on 2013. 12. 25..
 */
public class AttendanceCheckedEvent {
    private int mPostId;

    public AttendanceCheckedEvent(int postId) {
        mPostId = postId;
    }

    public int getPostId() {
        return mPostId;
    }
}
