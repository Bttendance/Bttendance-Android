package com.utopia.bttendance.helper;

import android.content.Context;

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.model.BTPersistent;
import com.utopia.bttendance.model.BTPreference;

import java.util.UUID;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class UUIDHelper {

    public static String ERROR = "uuid doesn't match error";

    public static String getUUID(Context context) {
        String uuidPers = BTPersistent.getUUID(context);
        String uuidPref = BTPreference.getUUID(context);

        BTDebug.LogInfo("uuid Persistent : " + uuidPers);
        BTDebug.LogInfo("uuid Preference : " + uuidPref);

        if (uuidPers == null && uuidPref == null) {
            String uuid = UUID.randomUUID().toString();
            BTPersistent.setUUID(context, uuid);
            BTPreference.setUUID(context, uuid);
            return uuid;
        } else if (uuidPers == null) {
            BTPersistent.setUUID(context, uuidPref);
            return uuidPref;
        } else if (uuidPref == null) {
            BTPreference.setUUID(context, uuidPers);
            return uuidPers;
        } else if (!uuidPers.equals(uuidPref)) {
            return ERROR;
        } else {
            return uuidPers;
        }
    }

    public static void setUUID(Context context) {
        String uuid = UUID.randomUUID().toString();
        BTPersistent.setUUID(context, uuid);
        BTPreference.setUUID(context, uuid);
    }
}
