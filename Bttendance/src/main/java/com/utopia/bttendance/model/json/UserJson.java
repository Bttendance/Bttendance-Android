package com.utopia.bttendance.model.json;

import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class UserJson {

    public int id;
    public String username;
    public String email;
    public String password;
    public String type;
    public String device_type;
    public String device_uuid;
    public String full_name;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
