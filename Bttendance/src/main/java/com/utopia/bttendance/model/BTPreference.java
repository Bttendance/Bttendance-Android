
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

    public static void setDefaultLanguage(Context ctx, String code, String name) {
        Editor edit = getInstance(ctx).edit();
        edit.putString("language_name", name);
        edit.putString("language_code", code);
        edit.commit();
    }

    public static String getDefaultLanguageCode(Context ctx) {
        String code = getInstance(ctx).getString("language_code", null);

        if (code == null) {
            code = Locale.getDefault().getLanguage();
            // Note that Java uses several deprecated two-letter codes.
            // The Hebrew ("he") language code is rewritten as "iw",
            // Indonesian ("id") as "in", and Yiddish ("yi") as "ji".
            // This rewriting happens even if you construct your own Locale
            // object, not just for instances returned by the various lookup
            // methods.

            // replace depreciated two-letter codes.
            code = code.replace("iw", "iw");
            code = code.replace("in", "id");
            code = code.replace("ji", "vi");
        }
        return code;
    }

    public static String getDefaultLanguageName(Context ctx) {
        String name = getInstance(ctx).getString("language_name", null);

        if (name == null)
            name = Locale.getDefault().getDisplayLanguage();

        return name;
    }

    private BTPreference() {
    }

}// end of class
