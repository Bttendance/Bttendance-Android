package com.bttendance.event;

/**
 * Loading Event has to be paired!
 * Once you show Loading, you have to hide Loading at some point.
 * This is because of BTActivity handles loading as count.
 *
 * Created by TheFinestArtist on 2013. 12. 27..
 */
public class LoadingEvent {

    private boolean mVisible;

    public LoadingEvent(boolean visible) {
        mVisible = visible;
    }

    public boolean getVisibility() {
        return mVisible;
    }
}