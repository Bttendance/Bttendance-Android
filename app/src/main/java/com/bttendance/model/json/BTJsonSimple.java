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

    //not done yey, but not using
    public JsonType getType() {
        if (this instanceof UserJson)
            return JsonType.User;
        else if (this instanceof UserJsonSimple)
            return JsonType.SimpleUser;

        else if (this instanceof CourseJson)
            return JsonType.Course;
        else if (this instanceof CourseJsonSimple)
            return JsonType.SimpleCourse;

        else if (this instanceof SchoolJson)
            return JsonType.School;
        else if (this instanceof SchoolJsonSimple)
            return JsonType.SimpleSchool;

        else if (this instanceof PostJson)
            return JsonType.Post;
        else if (this instanceof PostJsonSimple)
            return JsonType.SimplePost;

        return JsonType.Null;
    }

    public enum JsonType {
        User, SimpleUser,
        Course, SimpleCourse,
        School, SimpleSchool,
        Post, SimplePost,
        Device, SimpleDevice,
        Identification, SimpleIdentification,
        Setting, SimpleSetting,
        Question, SimpleQuestion,
        Attendance, SimpleAttendance,
        Clicker, SimpleClicker,
        Notice, SimpleNotice,
        Null
    }
}
