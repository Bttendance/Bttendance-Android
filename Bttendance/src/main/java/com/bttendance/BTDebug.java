package com.bttendance;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class BTDebug {

    public static final boolean DEBUG = true;
    public static final String TAG = "Bttendance";
    public static final String TAG_API = "API";

    private BTDebug() {
    }

    public static void LogError(String log) {
        if (DEBUG) {
            if (log != null)
                Log.e(TAG, log);
            else
                Log.e(TAG, "Log is null");
        }
    }

    public static void LogDebug(String log) {
        if (DEBUG)
            if (log != null)
                Log.d(TAG, log);
            else
                Log.d(TAG, "Log is null");
    }

    public static void LogInfo(String log) {
        if (DEBUG)
            if (log != null)
                Log.i(TAG, log);
            else
                Log.i(TAG, "Log is null");
    }

    public static void LogVerbose(String log) {
        if (DEBUG)
            if (log != null)
                Log.v(TAG, log);
            else
                Log.v(TAG, "Log is null");
    }

    public static void LogQueryAPI(String log) {
        if (DEBUG)
            if (log != null)
                Log.v(TAG_API, log);
            else
                Log.v(TAG_API, "Log is null");
    }

    public static void LogResponseAPI(String log) {
//        if (DEBUG)
//            if (log != null)
//                Log.i(TAG_API, log);
//            else
//                Log.i(TAG_API, "Log is null");
    }

    public static void Toast(Context context, String log) {
        if (DEBUG)
            if (log != null)
                Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Log is null", Toast.LENGTH_SHORT).show();
    }
}
