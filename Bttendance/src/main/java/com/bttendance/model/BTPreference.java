
package com.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bttendance.model.json.CourseJsonSimple;
import com.bttendance.model.json.SchoolJsonSimple;
import com.bttendance.model.json.UserJson;
import com.google.gson.Gson;

/**
 * Preference Helper
 *
 * @author The Finest Artist
 */
public class BTPreference {

    private static SharedPreferences mPref = null;
    private static Object mSingletonLock = new Object();

    private BTPreference() {
    }

    private static SharedPreferences getInstance(Context ctx) {
        synchronized (mSingletonLock) {
            if (mPref != null)
                return mPref;

            if (ctx != null) {
                mPref = ctx.getSharedPreferences("BTRef", Context.MODE_PRIVATE);
            }
            return mPref;
        }
    }

    // on Log out
    public static void clearUser(Context ctx) {
        Editor edit = getInstance(ctx).edit();
        edit.remove("user");
        edit.commit();
    }

    public static UserJson getUser(Context ctx) {
        String jsonStr = getInstance(ctx).getString("user", null);
        if (jsonStr == null)
            return null;
        Gson gson = new Gson();
        try {
            UserJson user = gson.fromJson(jsonStr, UserJson.class);
            if (user.device.uuid == null)
                user.device.uuid = user.device_uuid;
            return user;
        } catch (Exception e) {
            clearUser(ctx);
            return null;
        }
    }

    public static int getUserId(Context ctx) {
        UserJson user = getUser(ctx);
        if (user == null)
            return -1;
        return user.id;
    }

    public static void setUser(Context ctx, UserJson user) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(user);

        Editor edit = getInstance(ctx).edit();
        edit.putString("user", jsonStr);
        edit.commit();
    }

    public static String getUUID(Context ctx) {
        return getInstance(ctx).getString("uuid", null);
    }

    public static void setUUID(Context ctx, String uuid) {
        Editor edit = getInstance(ctx).edit();
        edit.putString("uuid", uuid);
        edit.commit();
    }

    public static CourseJsonSimple[] getCourses(Context context) {
        UserJson user = getUser(context);

        CourseJsonSimple[] newArray = new CourseJsonSimple[user.supervising_courses.length + user.attending_courses.length];

        int i = 0;

        for (CourseJsonSimple course : user.supervising_courses) {
            newArray[i] = course;
            i++;
        }

        for (CourseJsonSimple course : user.attending_courses) {
            newArray[i] = course;
            i++;
        }

        return newArray;

    }

    public static CourseJsonSimple getCourse(Context context, int courseID) {
        UserJson user = getUser(context);

        for (CourseJsonSimple course : user.supervising_courses)
            if (course.id == courseID)
                return course;

        for (CourseJsonSimple course : user.attending_courses)
            if (course.id == courseID)
                return course;

        return null;
    }

    public static SchoolJsonSimple getSchool(Context context, int schoolID) {
        UserJson user = getUser(context);

        for (SchoolJsonSimple school : user.employed_schools)
            if (school.id == schoolID)
                return school;

        for (SchoolJsonSimple school : user.enrolled_schools)
            if (school.id == schoolID)
                return school;

        return null;
    }

}// end of class
