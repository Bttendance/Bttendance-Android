
package com.utopia.bttendance.helper;

import android.content.Context;
import android.content.pm.PackageManager;

public class PackagesHelper {

    public static String KAKAOTALK = "com.kakao.talk";
    public static String KAKAOSTORY = "com.kakao.story";
    public static String FACEBOOK = "com.facebook.katana";
    public static String TWITTER = "com.twitter.android";
    public static String GOOGLE_PLUS = "com.google.android.apps.plus";
    public static String GMAIL = "com.google.android.gm";
    public static String PINTEREST = "com.pinterest";
    public static String TUMBLR = "com.tumblr";
    public static String FANCY = "com.thefancy.app";
    public static String FLIPBOARD = "flipboard.app";
    public static String MUSICOFFPAYED = "asia.utopia.musicoff.payed";

    public static boolean isInstalled(Context context, String packageUri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;

        try {
            pm.getPackageInfo(packageUri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }
}
