package com.bttendance.event.fragment;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowUpdateProfileEvent {

    private String mTitle;
    private String mMessage;
    private Type mType;

    public ShowUpdateProfileEvent(String title, String message, Type type) {
        mTitle = title;
        mMessage = message;
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {PROFILE_IMAGE, NAME, MAIL}
}
