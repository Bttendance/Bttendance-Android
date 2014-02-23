package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class UserJson extends BTJson {

    public String username;
    public String email;
    public String password;
    public String device_type;
    public String device_uuid;
    public String notification_key;
    public String full_name;
    public String profile_image;
    public int[] supervising_courses;
    public int[] attending_courses;
    public UserSchool[] employed_schools;
    public UserSchool[] enrolled_schools;

    public class UserSchool {
        public int id;
        public String key;
    }
}
