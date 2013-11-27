
package com.utopia.bttendance.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.utopia.bttendance.R;


/**
 * Beutiful Toast
 *
 * @author The Finest Artist
 */
public class BeautiLoading {

    public static void show(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    private static void showToast(final Context context, final String message, final int duration) {

        if (context == null)
            return;

        Runnable showToast = new Runnable() {

            @Override
            public void run() {

                Toast toast = new Toast(context);
                View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
                TextView messageTv = (TextView) view.findViewById(R.id.toast_message);
                messageTv.setText(message);
                toast.setView(view);
                toast.setGravity(Gravity.BOTTOM, 0, 80);
                toast.setDuration(duration);
                toast.show();
                ;
            }
        };

        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(showToast);
        } else {
            new Handler(context.getMainLooper()).post(showToast);
        }
    }
}//end of class
