package com.utopia.bttendance.helper;

import android.content.Context;

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.model.BTDatabase;
import com.utopia.bttendance.model.BTPreference;

import java.util.UUID;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class UUIDHelper {

    public static String ERROR = "uuid doesn't match error";

    public static String getUUID(Context context) {
        String uuidPref = BTPreference.getUUID(context);
        String uuidDB = BTDatabase.getUUID(context);

        if (uuidPref != null)
            uuidPref = uuidPref.replaceAll("\n", "");

        if (uuidDB != null)
            uuidDB = uuidDB.replaceAll("\n", "");

        if (uuidPref == null && uuidDB == null) {
            return resetUUID(context);
        } else if (uuidDB == null) {
            BTDatabase.setUUID(context, uuidPref);
            return uuidPref;
        } else if (uuidPref == null) {
            BTPreference.setUUID(context, uuidDB);
            return uuidDB;
        } else if (!uuidDB.equals(uuidPref)) {
            return ERROR;
        } else {
            return uuidDB;
        }
    }

    public static String resetUUID(Context context) {
        String uuid = UUID.randomUUID().toString();
        BTDatabase.setUUID(context, uuid);
        BTPreference.setUUID(context, uuid);
        return uuid;
    }
}
