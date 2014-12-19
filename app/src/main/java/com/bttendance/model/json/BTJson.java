package com.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTJson {

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toString() {
        return toJson();
    }
}