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
    public static SparseArray<SchoolJson> SchoolTable = new SparseArray<>();
    public static SparseArray<CourseJson> CourseTable = new SparseArray<>();

    public static SparseArray<SchoolJson> MySchoolTable = new SparseArray<>();
    public static SparseArray<CourseJson> MyCourseTable = new SparseArray<>();

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

        SchoolJson[] mySchools = BTDatabase.getMySchools(context);
        if (mySchools != null)
            for (SchoolJson school : mySchools)
                MySchoolTable.put(school.id, school);

        CourseJson[] myCourses = BTDatabase.getMyCourses(context);
        if (myCourses != null)
            for (CourseJson course : myCourses)
                MyCourseTable.put(course.id, course);
    }

    public static void clear() {
        Me = null;
        SchoolTable.clear();
        CourseTable.clear();
        MySchoolTable.clear();
        MyCourseTable.clear();
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

    public static void putMySchool(SchoolJson[] addingSchools) {
        for (SchoolJson schoolJson : addingSchools)
            MySchoolTable.put(schoolJson.id, schoolJson);
    }

    public static void putMyCourse(CourseJson[] addingCourses) {
        for (CourseJson courseJson : addingCourses)
            MyCourseTable.put(courseJson.id, courseJson);
    }

}