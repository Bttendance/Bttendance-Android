
package com.utopia.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
                mPref = ctx.getSharedPreferences("BTRef",
                        Context.MODE_PRIVATE);
            }
            return mPref;
        }
    }

    public static void clearAuth(Context ctx) {
        Editor edit = getInstance(ctx).edit();
        // edit.remove("username");
        edit.remove("auth_json");
        edit.commit();
    }

//    public static AuthJson getAuthJson(Context ctx) {
//        String jsonStr = getInstance(ctx).getString("auth_json", null);
//        if (jsonStr == null)
//            return null;
//        Gson gson = new Gson();
//        try {
//            return gson.fromJson(jsonStr, AuthJson.class);
//        } catch (Exception e) {
//            clearAuth(ctx);
//            return null;
//        }
//    }
//
//    public static void setAuthJson(Context ctx, AuthJson auth) {
//        Gson gson = new Gson();
//        String jsonStr = gson.toJson(auth);
//
//        Editor edit = getInstance(ctx).edit();
//        edit.putString("auth_json", jsonStr);
//        edit.commit();
//    }

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
