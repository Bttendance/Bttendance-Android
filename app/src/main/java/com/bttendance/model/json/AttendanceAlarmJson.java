package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 12/22/14.
 */
public class AttendanceAlarmJson extends BTJson {

    public int id;
    public int course_id;
    public int schedule_id;
    public int user_id;
    public String scheduled_for;
    public boolean manual;
    public boolean active;
}
