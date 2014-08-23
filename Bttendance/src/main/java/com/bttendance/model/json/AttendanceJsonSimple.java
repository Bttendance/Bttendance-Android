package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class AttendanceJsonSimple extends BTJsonSimple {

    public String type;
    public int[] checked_students;
    public int[] late_students;
    public int post;

    public int getStateInt(int userID) {

        for (int i = 0; i < checked_students.length; i++)
            if (checked_students[i] == userID)
                return 1;

        for (int i = 0; i < late_students.length; i++)
            if (late_students[i] == userID)
                return 2;

        return 0;
    }

    public int getAttendedCount() {
        return checked_students.length + late_students.length;
    }
}
