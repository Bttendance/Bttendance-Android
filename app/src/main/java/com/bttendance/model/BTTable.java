package com.bttendance.model;

import android.util.SparseArray;

import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.google.gson.Gson;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTTable {

    private static UserJson Me;
    public static SparseArray<SchoolJson> SchoolTable = new SparseArray<SchoolJson>();
    public static SparseArray<CourseJson> CourseTable = new SparseArray<CourseJson>();

    public static void setMe(UserJson me) {
        Me = me;
    }

    public static UserJson getMe() {
        String meString = Me.toJson();
        Gson gson = new Gson();
        try {
            return gson.fromJson(meString, UserJson.class);
        }  catch (Exception e) {
            return null;
        }
    }

    public static void putSchool(SchoolJson[] addingSchools) {
        for (SchoolJson schoolJson : addingSchools)
            SchoolTable.put(schoolJson.id, schoolJson);
    }

    public static void putCourse(CourseJson[] addingCourses) {
        for (CourseJson courseJson : addingCourses)
            CourseTable.put(courseJson.id, courseJson);
    }

}