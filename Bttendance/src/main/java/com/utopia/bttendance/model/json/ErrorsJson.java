package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class ErrorsJson {

    public String message;
    public String alert;
    public String toast;
    public String uuid;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
