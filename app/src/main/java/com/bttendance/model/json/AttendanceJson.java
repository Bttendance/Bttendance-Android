package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class AttendanceJson extends BTJson {

    public static String TYPE_AUTO = "auto";
    public static String TYPE_MANUAL = "manual";

    public String type;
    public int[] checked_students;
    public int[] late_students;
    public int[][] clusters;
    public PostJsonSimple post;
}