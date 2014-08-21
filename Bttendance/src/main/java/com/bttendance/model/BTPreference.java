package com.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bttendance.event.update.UpdateUserEvent;
import com.bttendance.model.json.CourseJsonArray;
import com.bttendance.model.json.CourseJsonSimple;
import com.bttendance.model.json.PostJsonArray;
import com.bttendance.model.json.QuestionJsonArray;
import com.bttendance.model.json.SchoolJsonArray;
import com.bttendance.model.json.UserJson;
import com.bttendance.model.json.UserJsonSimpleArray;
import com.google.gson.Gson;
import com.squareup.otto.BTEventBus;

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
        edit.remove("users");
        edit.remove("user");
        edit.commit();
    }

    public static UserJson getUser(Context ctx) {
        String jsonStr = getInstance(ctx).getString("users", null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            UserJson user = gson.fromJson(jsonStr, UserJson.class);
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
        edit.putString("users", jsonStr);
        edit.commit();

        BTEventBus.getInstance().post(new UpdateUserEvent());
    }

    public static SchoolJsonArray getAllSchools(Context ctx) {
        String jsonStr = getInstance(ctx).getString("all_schools", null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            SchoolJsonArray schoolJsonArray = gson.fromJson(jsonStr, SchoolJsonArray.class);
            return schoolJsonArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setAllSchools(Context ctx, SchoolJsonArray schoolJsonArray) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(schoolJsonArray);

        Editor edit = getInstance(ctx).edit();
        edit.putString("all_schools", jsonStr);
        edit.commit();
    }

    public static CourseJsonArray getCourses(Context ctx) {
        String jsonStr = getInstance(ctx).getString("courses", null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            CourseJsonArray courseJsonArray = gson.fromJson(jsonStr, CourseJsonArray.class);
            return courseJsonArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setCourses(Context ctx, CourseJsonArray courseJsonArray) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(courseJsonArray);

        Editor edit = getInstance(ctx).edit();
        edit.putString("courses", jsonStr);
        edit.commit();
    }

    public static PostJsonArray getPostsOfCourse(Context ctx, int courseID) {
        String jsonStr = getInstance(ctx).getString("posts_" + courseID, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            PostJsonArray postJsonArray = gson.fromJson(jsonStr, PostJsonArray.class);
            return postJsonArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setPostsOfCourse(Context ctx, PostJsonArray postJsonArray, int courseId) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(postJsonArray);

        Editor edit = getInstance(ctx).edit();
        edit.putString("posts_" + courseId, jsonStr);
        edit.commit();
    }

    public static UserJsonSimpleArray getStudentsOfCourse(Context ctx, int courseID) {
        String jsonStr = getInstance(ctx).getString("students_" + courseID, null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            UserJsonSimpleArray userJsonSimpleArray = gson.fromJson(jsonStr, UserJsonSimpleArray.class);
            return userJsonSimpleArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setStudentsOfCourse(Context ctx, UserJsonSimpleArray userJsonSimpleArray, int courseId) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(userJsonSimpleArray);

        Editor edit = getInstance(ctx).edit();
        edit.putString("students_" + courseId, jsonStr);
        edit.commit();
    }

    public static QuestionJsonArray getMyQuestions(Context ctx) {
        String jsonStr = getInstance(ctx).getString("my_questions", null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            QuestionJsonArray questionJsonArray = gson.fromJson(jsonStr, QuestionJsonArray.class);
            return questionJsonArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setMyQuestions(Context ctx, QuestionJsonArray questionJsonArray) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(questionJsonArray);

        Editor edit = getInstance(ctx).edit();
        edit.putString("my_questions", jsonStr);
        edit.commit();
    }

    public static int getLastSeenCourse(Context ctx) {
        int lastCourse = getInstance(ctx).getInt("last_course", 0);
        if (lastCourse == 0) {
            UserJson user = getUser(ctx);
            for (CourseJsonSimple course : user.supervising_courses)
                if (course.opened) {
                    setLastSeenCourse(ctx, course.id);
                    return course.id;
                }

            for (CourseJsonSimple course : user.attending_courses)
                if (course.opened) {
                    setLastSeenCourse(ctx, course.id);
                    return course.id;
                }
        }
        return lastCourse;
    }

    public static void setLastSeenCourse(Context ctx, int lastCourse) {
        Editor edit = getInstance(ctx).edit();
        edit.putInt("last_course", lastCourse);
        edit.commit();
    }

    public static boolean seenGuide(Context ctx) {
        Boolean seen = getInstance(ctx).getBoolean("seen_guide", false);

        if (!seen) {
            Editor edit = getInstance(ctx).edit();
            edit.putBoolean("seen_guide", true);
            edit.commit();
        }

        return seen;
    }

    public static String getUUID(Context ctx) {
        return getInstance(ctx).getString("uuid", null);
    }

    public static void setUUID(Context ctx, String uuid) {
        Editor edit = getInstance(ctx).edit();
        edit.putString("uuid", uuid);
        edit.commit();
    }

}// end of class
