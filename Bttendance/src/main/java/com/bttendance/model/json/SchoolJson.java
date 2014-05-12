package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class SchoolJson extends BTJson {

    public String name;
    public String logo_image;
    public String website;
    public String type;
    public SerialJsonSimple[] serials;
    public CourseJsonSimple[] courses;
    public UserJsonSimple[] professors;
    public UserJsonSimple[] students;

//    @InstantText(viewId = R.id.name)
//    public String getName() {
//        return name;
//    }
//
//    @InstantText(viewId = R.id.website)
//    public String getWebsite() {
//        return website;
//    }
}