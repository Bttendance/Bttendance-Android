package com.utopia.bttendance.helper;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by TheFinestArtist on 2013. 11. 22..
 */
public class LanguageHelper {

    public static String getDefaultLanguageCode() {
        String code = Locale.getDefault().getLanguage();
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
        return code;
    }

    public static String getDefaultLanguageName() {
        return Locale.getDefault().getDisplayLanguage();
    }

    public static String getDefaultTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return String.valueOf(tz.getID());
    }
}
