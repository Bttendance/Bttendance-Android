package com.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bttendance.BuildConfig;

/**
 * Preference Helper
 *
 * @author The Finest Artist
 */
public class BTPreference {

    private static final String PREF_NAME = "BTRef";
    private static final String LAST_COURSE = "lastCourse";
    private static final String APP_VERSION = "appVersion";
    private static final String MAC_ADDRESS = "macAddress";
    private static final String NOTIFICATION_KEY = "notificationKey";

    private static SharedPreferences mPref = null;
    private static final Object mSingletonLock = new Object();

    private BTPreference() {
    }

    private static SharedPreferences getInstance(Context ctx) {
        synchronized (mSingletonLock) {
            if (mPref != null)
                return mPref;

            if (ctx != null) {
                mPref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            }
            return mPref;
        }
    }

    // Log Out
    public static void clearPref(Context ctx) {
        SharedPreferences.Editor edit = getInstance(ctx).edit();
        edit.remove(APP_VERSION);
        edit.apply();
    }

    // App Version
    public static boolean needAppVersionUpdate(Context ctx) {
        int oldVersionCode = getInstance(ctx).getInt(APP_VERSION, 0);
        int newVersionCode = BuildConfig.VERSION_CODE;

        if (oldVersionCode != newVersionCode) {
            Editor edit = getInstance(ctx).edit();
            edit.putInt(APP_VERSION, newVersionCode);
            edit.apply();
            return true;
        }

        return false;
    }

    // Mac Address
    public static String getMacAddress(Context ctx) {
        return getInstance(ctx).getString(MAC_ADDRESS, null);
    }

    public static void setMacAddress(Context ctx, String macAddress) {
        if (macAddress == null)
            return;

        Editor edit = getInstance(ctx).edit();
        edit.putString(MAC_ADDRESS, macAddress);
        edit.apply();
    }

    // Notification Key
    public static String getNotificationKey(Context ctx) {
        return getInstance(ctx).getString(NOTIFICATION_KEY, null);
    }

    public static void setNotificationKey(Context ctx, String notificationKey) {
        if (notificationKey == null)
            return;

        Editor edit = getInstance(ctx).edit();
        edit.putString(NOTIFICATION_KEY, notificationKey);
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
