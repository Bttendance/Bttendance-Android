package com.bttendance.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by TheFinestArtist on 12/11/14.
 */
public class ViewHelper {

    public static void setBackground(Context context, View view, int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            view.setBackgroundDrawable(context.getResources().getDrawable(id));
        else
            view.setBackground(context.getResources().getDrawable(id));
    }

    public static void setBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            view.setBackgroundDrawable(d);
        else
            view.setBackground(d);
    }
}
