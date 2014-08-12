package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class AttendanceJson extends BTJson {

    public int[] checked_students;
    public int[] late_students;
    public int[][] clusters;
    public PostJsonSimple post;
}