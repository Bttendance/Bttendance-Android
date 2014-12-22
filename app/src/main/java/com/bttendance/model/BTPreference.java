package com.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bttendance.model.json.UserJson;
import com.google.gson.Gson;

/**
 * Preference Helper
 *
 * @author The Finest Artist
 */
public class BTPreference {

    private static String USER = "user";
    private static String LAST_COURSE = "lastCourse";
    private static final String APP_VERSION = "appVersion";

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
        edit.remove(USER);
        edit.apply();
    }

    public static boolean hasAuth(Context ctx) {
        String jsonStr = getInstance(ctx).getString(USER, null);
        if (jsonStr == null)
            return false;

        return true;
    }

    public static UserJson getUser(Context ctx) {
        String jsonStr = getInstance(ctx).getString(USER, null);
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

    public static void setUser(Context ctx, UserJson user) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(user);

        Editor edit = getInstance(ctx).edit();
        edit.putString(USER, jsonStr);
        edit.apply();
    }

    // First Main View
    public static int getLastSeenCourse(Context ctx) {
        return getInstance(ctx).getInt(LAST_COURSE, Integer.MIN_VALUE);
    }

    public static void setLastSeenCourse(Context ctx, int lastCourse) {
        Editor edit = getInstance(ctx).edit();
        edit.putInt(LAST_COURSE, lastCourse);
        edit.apply();
    }

}
