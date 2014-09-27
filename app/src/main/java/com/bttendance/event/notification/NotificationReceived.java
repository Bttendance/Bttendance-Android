package com.bttendance.event.notification;

/**
 * Created by TheFinestArtist on 2014. 8. 25..
 */
public class NotificationReceived {

    private String mType;
    private String mTitle;
    private String mMessage;
    private String mCourseID;

    public NotificationReceived(String type, String title, String message, String courseID) {
        mType = type;
        mTitle = title;
        mMessage = message;
        mCourseID = courseID;
    }

    public String getType() {
        return mType;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getCourseID() {
        return mCourseID;
    }
}
