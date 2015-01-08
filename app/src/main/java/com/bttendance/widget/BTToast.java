package com.bttendance.widget;

import android.app.Activity;

import com.bttendance.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;

/**
 * Bttendance Toast
 *
 * @author The Finest Artist
 */
public class BTToast {

    public static void show(Activity activity, String message) {
        if (activity == null)
            return;

        Snackbar.with(activity)
                .type(SnackbarType.MULTI_LINE)
                .text(message)
                .textColor(activity.getResources().getColor(R.color.bttendance_white))
                .color(activity.getResources().getColor(R.color.bttendance_silver))
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .show(activity);
    }

    public static void alert(Activity activity, String message) {
        if (activity == null)
            return;

        Snackbar.with(activity)
                .type(SnackbarType.MULTI_LINE)
                .text(message)
                .textColor(activity.getResources().getColor(R.color.bttendance_white))
                .color(activity.getResources().getColor(R.color.bttendance_red))
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .show(activity);
    }

}//end of class
