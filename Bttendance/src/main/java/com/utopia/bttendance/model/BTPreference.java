
package com.utopia.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.utopia.bttendance.model.json.UserJson;

import java.util.Locale;

/**
 * Preference Helper
 *
 * @author The Finest Artist
 */
public class BTPreference {

    private static SharedPreferences mPref = null;
    private static SharedPreferences mNotiPref = null;
    private static Object mSingletonLock = new Object();

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
    public static void clearAuth(Context ctx) {
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
            return gson.fromJson(jsonStr, UserJson.class);
        } catch (Exception e) {
            clearAuth(ctx);
            return null;
        }
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

    private BTPreference() {
    }

}// end of class
