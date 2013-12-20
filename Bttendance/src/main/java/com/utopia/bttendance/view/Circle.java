package com.utopia.bttendance.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

import com.utopia.bttendance.R;
import com.utopia.bttendance.helper.ScreenHelper;
import com.utopia.bttendance.model.BTKey;

/**
 * Created by TheFinestArtist on 2013. 11. 25..
 */
public class Circle extends View {
    private static final int INIT_ALPHA = 200;
    private static final int FIN_ALPHA = 250;
    private static final int INIT_RADIUS = 220;
    Paint paint;
    float mX, mY, mMulti, mDense;
    BTKey.Type mType;
    boolean initialized = false;
    float mInitX;
    float mCenterX, mCenterY, mMaxRadius;

    public Circle(Context context, BTKey.Type type) {
        super(context);
        mType = type;
        switch (type) {
            case PROFESSOR:
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(context.getResources().getColor(R.color.bttendance_navy));
                paint.setAlpha(INIT_ALPHA);
                break;
            case STUDENT:
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(context.getResources().getColor(R.color.bttendance_cyan));
                paint.setAlpha(INIT_ALPHA);
            default:
                break;
        }

        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_HIGH:
                mDense = 0.75f;
                break;
            default:
                mDense = 1.0f;
                break;
        }

        mCenterX = ScreenHelper.getWidth(getContext()) / 2.0f;
        mCenterY = (ScreenHelper.getHeight(getContext()) - ScreenHelper.getSBHeight(getContext())) / 2.0f;
        int width = ScreenHelper.getWidth(getContext());
        int height = ScreenHelper.getHeight(getContext());
        mMaxRadius = (float) Math.sqrt(width * width + height * height) / 2.0f + 80.0f;
    }

    protected void onDraw(Canvas canvas) {
        if (initialized) {
            float radius = INIT_RADIUS * mMulti * mDense;
            if (radius > mMaxRadius)
                radius = mMaxRadius;
            canvas.drawCircle(mX, mY, radius, paint);
        }
    }

    public void initView(float x, float y, int progress) {
        initialized = true;
        switch (mType) {
            case PROFESSOR:
                mInitX = x;
                break;
            case STUDENT:
            default:
                mInitX = ScreenHelper.getWidth(getContext()) - x;
                break;
        }
        updateView(x, y, progress);
    }

    public void updateView(float x, float y, int progress) {
        if (!initialized)
            return;

        int alpha;
        switch (mType) {
            case PROFESSOR:
                mMulti = 1.0f + ((float) progress) / 20.0f;
                alpha = progress == 0 ? INIT_ALPHA : FIN_ALPHA;
                mX = x - mInitX;
                if (mX > mCenterX)
                    mX = mCenterX;
                mY = (y - mCenterY) * (mCenterX - mX) / mCenterX + mCenterY;
                break;
            case STUDENT:
            default:
                mMulti = 1.0f + (100.0f - ((float) progress)) / 20.0f;
                alpha = progress == 100 ? INIT_ALPHA : FIN_ALPHA;
                mX = x + mInitX;
                if (mX < mCenterX)
                    mX = mCenterX;
                mY = (y - mCenterY) * (mX - mCenterX) / mCenterX + mCenterY;
                break;
        }
        paint.setAlpha(alpha);
        invalidate();
    }
}
