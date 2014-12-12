package com.bttendance.view;

import android.app.Activity;

import com.bttendance.R;
import com.nispok.snackbar.Snackbar;

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
                .text(message)
                .textColor(activity.getResources().getColor(R.color.bttendance_white))
                .color(activity.getResources().getColor(R.color.bttendance_silver))
                .show(activity);
    }

    public static void alert(Activity activity, String message) {
        if (activity == null)
            return;

        Snackbar.with(activity)
                .text(message)
                .textColor(activity.getResources().getColor(R.color.bttendance_white))
                .color(activity.getResources().getColor(R.color.bttendance_red))
                .show(activity);
    }

}//end of class
