package com.bttendance.model;

import android.content.Context;
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

    public static void init(Context context) {
        Me = BTDatabase.getUser(context);

        SchoolJson[] schools = BTDatabase.getSchools(context);
        if (schools != null)
            for (SchoolJson school : schools)
                SchoolTable.put(school.id, school);

        CourseJson[] courses = BTDatabase.getCourses(context);
        if (courses != null)
            for (CourseJson course : courses)
                CourseTable.put(course.id, course);
    }

    public static void setMe(UserJson user) {
        Me = user;
    }

    public static UserJson getMe() {
        try {
            String meString = Me.toJson();
            Gson gson = new Gson();
            return gson.fromJson(meString, UserJson.class);
        } catch (Exception e) {
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