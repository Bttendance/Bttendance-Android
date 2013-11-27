package com.utopia.bttendance.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by TheFinestArtist on 2013. 11. 26..
 */
public class SeekBar_ extends SeekBar {
    Drawable mThumb;

    public SeekBar_(Context context) {
        super(context);
    }

    public SeekBar_(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBar_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }
}
