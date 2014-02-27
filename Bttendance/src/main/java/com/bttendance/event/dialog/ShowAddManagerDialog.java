package com.bttendance.event.dialog;

/**
 * Created by TheFinestArtist on 2013. 12. 10..
 */
public class ShowAddManagerDialog {
    private String mUsername;
    private String mFullname;
    private int mCourseID;
    private String mCoursename;

    public ShowAddManagerDialog(String username, String fullname, int courseID, String coursename) {
        mUsername = username;
        mFullname = fullname;
        mCourseID = courseID;
        mCoursename = coursename;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getFullname() {
        return mFullname;
    }

    public int getCourseID() {
        return mCourseID;
    }

    public String getCoursename() { return mCoursename; }
}
