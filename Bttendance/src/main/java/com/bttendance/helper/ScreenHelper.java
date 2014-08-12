/**
 * Copyright 2013 The Finest Artist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bttendance.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.bttendance.R;

public class ScreenHelper {

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getWidth(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        } else
            return display.getWidth();
    }

    public static int getNaviDrawerWidth(Context context) {
        int maxMenuWidth = context.getResources().getDimensionPixelSize(R.dimen.side_menu_max_width);
        int behindOffset = Math.min(ScreenHelper.getWidth(context) * 5 / 6, maxMenuWidth);
        return behindOffset;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    // Whole Screen includes StatusBar and ActionBar
    public static int getHeight(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.y;
        } else
            return display.getHeight();
    }

    // StatusBar
    public static int getSBHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // ActionBar
    public static int getABHeight(Context context) {

        int actionBarHeight = 0;
        TypedValue tva = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tva, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tva.data, context
                        .getResources()
                        .getDisplayMetrics());
        } else if (context.getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize,
                tva,
                true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tva.data, context
                    .getResources()
                    .getDisplayMetrics());
        }
        return actionBarHeight;
    }

    // Content without StatusBar and ActionBar
    public static int getContentHeight(Context context) {
        return getHeight(context) - getSBHeight(context) - getABHeight(context);
    }
}
