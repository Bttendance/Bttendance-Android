
package com.bttendance.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

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

    public static void updateApp(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
