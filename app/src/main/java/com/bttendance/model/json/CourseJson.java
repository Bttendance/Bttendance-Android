package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 12/19/14.
 */
public class CourseJson extends BTJson {

    public int id;
    public String name;
    public String instructor_name;
    public String code;
    public boolean open;
    public String information;
    public String start_date;
    public String end_date;
    public int attending_users_count;

    public SimpleSchoolJson school;
}
