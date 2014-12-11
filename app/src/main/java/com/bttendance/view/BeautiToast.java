package com.bttendance.view;

import android.app.Activity;

import com.bttendance.R;
import com.nispok.snackbar.Snackbar;


/**
 * Beutiful Toast
 *
 * @author The Finest Artist
 */
public class BeautiToast {

    public static void show(Activity activity, String message) {

        if (activity == null)
            return;

        Snackbar.with(activity)
                .text(message)
                .textColor(activity.getResources().getColor(R.color.bttendance_white))
                .color(activity.getResources().getColor(R.color.bttendance_silver))
//                .actionLabel("Action")
//                .actionColor(Color.RED)
//                .actionListener(null)
                .show(activity);
    }

}//end of class
