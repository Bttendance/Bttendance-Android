package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class SchoolJson {

    public int id;
    public String name;
    public String logo_image;
    public String website;
    public int[] courses;
    public int creator;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
