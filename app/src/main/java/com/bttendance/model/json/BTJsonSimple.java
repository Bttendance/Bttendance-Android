package com.bttendance.model.json;

import com.google.gson.Gson;

import io.realm.RealmObject;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class BTJsonSimple extends RealmObject {

    public int id;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toString() {
        return toJson();
    }

}
