package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class ValidationJson {

    public boolean validate;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
