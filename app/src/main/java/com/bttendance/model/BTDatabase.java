package com.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.bttendance.helper.SparseArrayHelper;
import com.bttendance.model.database.CourseJsons;
import com.bttendance.model.database.SchoolJsons;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PreferencesJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by TheFinestArtist on 1/2/15.
 */
public class BTDatabase {

    private static final String DB_NAME = "BTDatabase";
    private static final String USER = "user";
    private static final String PREFERENCES = "preferences";
    private static final String SCHOOLS = "schools";
    private static final String COURSES = "courses";
    private static final String MY_SCHOOLS = "mySchools";
    private static final String MY_COURSES = "myCourses";

    private static SharedPreferences mPref = null;
    private static final Object mSingletonLock = new Object();

    private BTDatabase() {
    }

    private static SharedPreferences getInstance(Context ctx) {
        synchronized (mSingletonLock) {
            if (mPref != null)
                return mPref;

            if (ctx != null) {
                mPref = ctx.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
            }
            return mPref;
        }
    }

    // User
    public static void clearUser(Context ctx) {
        BTPreference.clearPref(ctx);
        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.remove(USER);
        edit.apply();
    }

    public static UserJson getUser(Context ctx) {
        String jsonStr = getInstance(ctx).getString(USER, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            return gson.fromJson(jsonStr, UserJson.class);
        } catch (Exception e) {
            clearUser(ctx);
            return null;
        }
    }

    public static void setUser(Context ctx, UserJson user) {
        if (user == null)
            return;

        Gson gson = new Gson();
        String jsonStr = gson.toJson(user);

        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.putString(USER, jsonStr);
        edit.apply();
    }


    // Preferences
    public static PreferencesJson getPreference(Context ctx) {
        String jsonStr = getInstance(ctx).getString(PREFERENCES, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            return gson.fromJson(jsonStr, PreferencesJson.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setPreference(Context ctx, PreferencesJson preferences) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(preferences);

        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.putString(PREFERENCES, jsonStr);
        edit.apply();
    }

    // Schools
    public static SchoolJson[] getSchools(Context ctx) {
        String jsonStr = getInstance(ctx).getString(SCHOOLS, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            SchoolJsons schoolJsons = gson.fromJson(jsonStr, SchoolJsons.class);
            return schoolJsons == null ? null : schoolJsons.schools;
        } catch (Exception e) {
            return null;
        }
    }

    public static SchoolJson getSchool(Context ctx, int schoolId) {
        SchoolJson[] schools = getSchools(ctx);
        if (schools != null)
            for (SchoolJson schoolJson : schools)
                if (schoolJson.id == schoolId)
                    return schoolJson;

        return null;
    }

    public static void putSchools(Context ctx, SchoolJson[] addingSchools) {
        SchoolJson[] schools = getSchools(ctx);
        if (schools == null)
            schools = new SchoolJson[0];

        SparseArray<SchoolJson> schoolArray = new SparseArray<>();
        for (SchoolJson school : schools)
            schoolArray.put(school.id, school);

        for (SchoolJson school : addingSchools)
            schoolArray.put(school.id, school);

        schools = SparseArrayHelper.asArrayList(schoolArray).toArray(schools);

        SchoolJsons schoolJsons = new SchoolJsons(schools);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(schoolJsons);

        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.putString(SCHOOLS, jsonStr);
        edit.apply();
    }

    // My Schools
    public static SchoolJson[] getMySchools(Context ctx) {
        String jsonStr = getInstance(ctx).getString(MY_SCHOOLS, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            SchoolJsons schoolJsons = gson.fromJson(jsonStr, SchoolJsons.class);
            return schoolJsons == null ? null : schoolJsons.schools;
        } catch (Exception e) {
            return null;
        }
    }

    public static void putMySchools(Context ctx, SchoolJson[] addingSchools) {
        SchoolJson[] schools = getMySchools(ctx);
        if (schools == null)
            schools = new SchoolJson[0];

        SparseArray<SchoolJson> schoolArray = new SparseArray<>();
        for (SchoolJson school : schools)
            schoolArray.put(school.id, school);

        for (SchoolJson school : addingSchools)
            schoolArray.put(school.id, school);

        schools = SparseArrayHelper.asArrayList(schoolArray).toArray(schools);

        SchoolJsons schoolJsons = new SchoolJsons(schools);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(schoolJsons);

        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.putString(MY_SCHOOLS, jsonStr);
        edit.apply();
    }

    // Courses
    public static CourseJson[] getCourses(Context ctx) {
        String jsonStr = getInstance(ctx).getString(COURSES, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            CourseJsons courseJsons = gson.fromJson(jsonStr, CourseJsons.class);
            return courseJsons == null ? null : courseJsons.courses;
        } catch (Exception e) {
            return null;
        }
    }

    public static CourseJson getCourse(Context ctx, int courseId) {
        CourseJson[] courses = getCourses(ctx);
        if (courses != null)
            for (CourseJson courseJson : courses)
                if (courseJson.id == courseId)
                    return courseJson;

        return null;
    }

    public static void putCourses(Context ctx, CourseJson[] addingCourses) {
        CourseJson[] courses = getCourses(ctx);
        if (courses == null)
            courses = new CourseJson[0];

        SparseArray<CourseJson> courseArray = new SparseArray<>();
        for (CourseJson course : courses)
            courseArray.put(course.id, course);

        for (CourseJson course : addingCourses)
            courseArray.put(course.id, course);

        courses = SparseArrayHelper.asArrayList(courseArray).toArray(courses);

        CourseJsons courseJsons = new CourseJsons(courses);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(courseJsons);

        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.putString(COURSES, jsonStr);
        edit.apply();
    }

    // My Courses
    public static CourseJson[] getMyCourses(Context ctx) {
        String jsonStr = getInstance(ctx).getString(MY_COURSES, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            CourseJsons courseJsons = gson.fromJson(jsonStr, CourseJsons.class);
            return courseJsons == null ? null : courseJsons.courses;
        } catch (Exception e) {
            return null;
        }
    }

    public static void putMyCourses(Context ctx, CourseJson[] addingCourses) {
        CourseJson[] courses = getMyCourses(ctx);
        if (courses == null)
            courses = new CourseJson[0];

        SparseArray<CourseJson> courseArray = new SparseArray<>();
        for (CourseJson course : courses)
            courseArray.put(course.id, course);

        for (CourseJson course : addingCourses)
            courseArray.put(course.id, course);

        courses = SparseArrayHelper.asArrayList(courseArray).toArray(courses);

        CourseJsons courseJsons = new CourseJsons(courses);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(courseJsons);

        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.putString(MY_COURSES, jsonStr);
        edit.apply();
    }

}
