package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class UserJson extends BTJson {

    public String username;
    public String username_lower;
    public String email;
    public String password;
    public String full_name;
    public String profile_image;
    public DeviceJsonSimple device;
    public CourseJsonSimple[] supervising_courses;
    public CourseJsonSimple[] attending_courses;
    public SchoolJsonSimple[] employed_schools;
    public SerialJsonSimple[] serials;
    public SchoolJsonSimple[] enrolled_schools;
    public IdentificationJsonSimple[] identifications;

    public String device_uuid;
}