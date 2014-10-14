package com.bttendance.event.attendance;

/**
 * Created by TheFinestArtist on 2014. 10. 14..
 */
public class AttdToggleEvent {

    private int studentID;

    public AttdToggleEvent(int studentID) {
        this.studentID = studentID;
    }

    public int getStudentID() {
        return studentID;
    }

}
