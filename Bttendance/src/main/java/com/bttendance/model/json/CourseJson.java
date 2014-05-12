package com.bttendance.model.json;

import com.bttendance.helper.DateHelper;

import java.util.Date;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class CourseJson extends BTJson {

    public String name;
    public String number;
    public String professor_name;
    public SchoolJsonSimple school;
    public UserJsonSimple[] managers;
    public UserJsonSimple[] students;
    public PostJsonSimple[] posts;
    public int students_count;
    public String attdCheckedAt;
    public int clicker_usage;
    public int notice_usage;

    public String grade;

    public Date getAttdCheckedDate() {
        return DateHelper.getDate(attdCheckedAt);
    }
}