package com.bttendance.event.clicker;

/**
 * Created by TheFinestArtist on 2014. 5. 15..
 */
public class ClickerClickEvent {

    private int mPostID;
    private int mChoice;

    public ClickerClickEvent(int postID, int choice) {
        mPostID = postID;
        mChoice = choice;
    }

    public int getPostID() {
        return mPostID;
    }

    public int getChoice() {
        return mChoice;
    }
}
