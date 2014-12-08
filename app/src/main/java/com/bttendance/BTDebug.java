package com.bttendance;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class BTDebug {

    public static final boolean DEBUG = false;
    public static final String TAG_BTTENDANCE = "BTTENDANCE";
    public static final String TAG_API = "API";

    private BTDebug() {
    }

    public static void LogError(String log) {
        if (DEBUG) {
            if (log != null)
                Log.e(TAG_BTTENDANCE, log);
            else
                Log.e(TAG_BTTENDANCE, "Log is null");
        }
    }

    public static void LogDebug(String log) {
        if (DEBUG)
            if (log != null)
                Log.d(TAG_BTTENDANCE, log);
            else
                Log.d(TAG_BTTENDANCE, "Log is null");
    }

    public static void LogDebug(String tag, String log) {
        if (DEBUG)
            if (log != null)
                Log.d(tag, log);
            else
                Log.d(tag, "Log is null");
    }

    public static void LogInfo(String log) {
        if (DEBUG)
            if (log != null)
                Log.i(TAG_BTTENDANCE, log);
            else
                Log.i(TAG_BTTENDANCE, "Log is null");
    }

    public static void LogVerbose(String log) {
        if (DEBUG)
            if (log != null)
                Log.v(TAG_BTTENDANCE, log);
            else
                Log.v(TAG_BTTENDANCE, "Log is null");
    }

    public static void LogQueryAPI(String log) {
        if (DEBUG)
            if (log != null)
                Log.i(TAG_API, log);
            else
                Log.i(TAG_API, "Log is null");
    }

    public static void LogResponseAPI(String log) {
        if (DEBUG)
            if (log != null)
                Log.v(TAG_API, log);
            else
                Log.v(TAG_API, "Log is null");
    }

    public static void Toast(Context context, String log) {
        if (DEBUG)
            if (log != null)
                Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Log is null", Toast.LENGTH_SHORT).show();
    }
}
