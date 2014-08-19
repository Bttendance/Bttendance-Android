package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class CourseJson extends BTJson {

    public String name;
    public String professor_name;
    public SchoolJsonSimple school;
    public UserJsonSimple[] managers;
    public int students_count;
    public int posts_count;
    public String code;
    public boolean opened;

    public int attendance_rate;
    public int clicker_rate;
    public int notice_unseen;

    public UserJsonSimple getManager(int managerID) {

        for (UserJsonSimple manager : managers)
            if (manager.id == managerID)
                return manager;

        return null;
    }

}