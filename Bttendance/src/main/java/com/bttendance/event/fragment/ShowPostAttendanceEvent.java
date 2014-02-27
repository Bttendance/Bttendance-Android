package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowPostAttendanceEvent {

    private int mPostId;

    public ShowPostAttendanceEvent(int postId) {
        mPostId = postId;
    }

    public int getPostId() {
        return mPostId;
    }

}
