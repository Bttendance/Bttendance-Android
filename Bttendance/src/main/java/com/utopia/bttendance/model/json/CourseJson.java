package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class CourseJson {

    public int id;
    public String name;
    public String number;
    public boolean onGoing;
    public int school;
    public int professor;
    public int[] students;
    public int[] posts;
    public String professor_name;
    public String school_name;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
