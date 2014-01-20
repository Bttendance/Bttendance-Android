
package com.bttendance.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * DipPixelHelper
 *
 * @author The Finest Artist
 */
public class DipPixelHelper {

    public static float getPixel(Context context, int dip) {
        Resources r = context.getResources();
        float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return pix;
    }
}
