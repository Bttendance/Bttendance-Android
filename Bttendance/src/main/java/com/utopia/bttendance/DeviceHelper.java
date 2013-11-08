package com.utopia.bttendance;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Created by TheFinestArtist on 2013. 11. 3..
 */
public class DeviceHelper {

    public static String getId(Activity activity) {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getMacAddress(Activity activity) {
        WifiManager wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wm.getConnectionInfo();
        return wInfo.getMacAddress();
    }
}
