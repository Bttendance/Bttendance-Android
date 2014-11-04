package com.bttendance.model.json;

import com.bttendance.helper.DateHelper;
import com.bttendance.helper.IntArrayHelper;

import java.util.Date;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class AttendanceJsonSimple extends BTJson {

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

    public void toggleStatus(int userID) {

        int[] checked = new int[0];
        int[] lated = new int[0];

        boolean check = false;
        boolean late = false;

        for (int i = 0; i < checked_students.length; i++) {
            if (checked_students[i] == userID) {
                check = true;
            } else {
                checked = IntArrayHelper.add(checked, checked_students[i]);
            }
        }

        for (int i = 0; i < late_students.length; i++) {
            if (late_students[i] == userID) {
                late = true;
            } else {
                lated = IntArrayHelper.add(lated, late_students[i]);
            }
        }

        if (check) {
            lated = IntArrayHelper.add(lated, userID);
        } else if (!late) {
            checked = IntArrayHelper.add(checked, userID);
        }

        checked_students = checked;
        late_students = lated;
    }

    public int getAttendedCount() {
        return checked_students.length + late_students.length;
    }
}
