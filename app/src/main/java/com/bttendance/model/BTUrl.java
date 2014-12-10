package com.bttendance.model;

import android.content.Context;

import com.bttendance.BuildConfig;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class BTUrl {
    public final static String TERMS_KR = "http://www.bttendance.com/terms";
    public final static String PRIVACY_KR = "http://www.bttendance.com/privacy";
    public final static String TERMS_EN = "http://www.bttendance.com/terms-en";
    public final static String PRIVACY_EN = "http://www.bttendance.com/privacy-en";
    public final static String PARTNERSHIP = "http://www.bttendance.com/contact";

    public static String getGuideClicker(Context context) {
        String locale = context.getResources().getConfiguration().locale.getLanguage();
        String url = "http://www.bttendance.com/api/guide/clicker?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + BuildConfig.VERSION_NAME;
        return url;
    }

    public static String getGuideAttendance(Context context) {
        String locale = context.getResources().getConfiguration().locale.getLanguage();
        String url = "http://www.bttendance.com/api/guide/attendance?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + BuildConfig.VERSION_NAME;
        return url;
    }

    public static String getGuideCurious(Context context) {
        String locale = context.getResources().getConfiguration().locale.getLanguage();
        String url = "http://www.bttendance.com/api/guide/curious?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + BuildConfig.VERSION_NAME;
        return url;
    }

    public static String getGuideNotice(Context context) {
        String locale = context.getResources().getConfiguration().locale.getLanguage();
        String url = "http://www.bttendance.com/api/guide/notice?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + BuildConfig.VERSION_NAME;
        return url;
    }
}
