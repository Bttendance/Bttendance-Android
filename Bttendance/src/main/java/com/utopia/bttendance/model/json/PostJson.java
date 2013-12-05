package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class PostJson {

    public int id;
    public String title;
    public String message;
    public boolean on_going;
    public int author;
    public int course;
    public int[] checks;
    public String author_name;
    public String course_name;
    public String course_number;
    public String school_name;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
