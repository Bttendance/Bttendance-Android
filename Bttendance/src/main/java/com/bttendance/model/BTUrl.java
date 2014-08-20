package com.bttendance.model;

import android.content.Context;

import com.bttendance.R;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class BTUrl {
    public final static String TERMS_KR = "http://www.bttendance.com/terms";
    public final static String PRIVACY_KR = "http://www.bttendance.com/privacy";
    public final static String TERMS_EN = "http://www.bttendance.com/terms-en";
    public final static String PRIVACY_EN = "http://www.bttendance.com/privacy-en";
    public final static String PARTNERSHIP = "http://www.bttendance.com/contact";

    public static String getTutorialClicker(Context context) {
        String locale = context.getResources().getConfiguration().locale.getCountry();
        String url = "http://www.bttd.co/tutorial/clicker?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + context.getString(R.string.app_version);
        return url;
    }

    public static String getTutorialAttendance(Context context) {
        String locale = context.getResources().getConfiguration().locale.getCountry();
        String url = "http://www.bttd.co/tutorial/attendance?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + context.getString(R.string.app_version);
        return url;
    }

    public static String getTutorialNotice(Context context) {
        String locale = context.getResources().getConfiguration().locale.getCountry();
        String url = "http://www.bttd.co/tutorial/notice?" +
                "device_type=android" +
                "&locale=" + locale +
                "&app_version=" + context.getString(R.string.app_version);
        return url;
    }
}
