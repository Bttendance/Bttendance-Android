package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class PostJson extends BTJson {

    public String type;
    public String message;
    public UserJsonSimple author;
    public CourseJsonSimple course;
    public AttendanceJsonSimple attendance;
    public ClickerJsonSimple clicker;
    public NoticeJsonSimple notice;

}