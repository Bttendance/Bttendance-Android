package com.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class BTJsonSimple {

    public int id;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toString() {
        return toJson();
    }

}
