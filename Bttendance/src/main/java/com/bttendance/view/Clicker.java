package com.bttendance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.bttendance.R;
import com.bttendance.helper.DipPixelHelper;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class Clicker extends View {

    /**
     * Duration
     */
    public static final int PROGRESS_DURATION = 65000;
    private static final int BLINK_DURATION = 1000;
    /**
     * Dimension
     */
    private static final int SMALL_SIZE = 52; //in dp
    private static final float SMALL_WHEEL_RADIUS = 24.2f; //in dp (52)
    /**
     * Type
     */
    private static final int A = 0;
    private static final int B = 1;
    private static final int C = 2;
    private static final int D = 3;
    private static final int E = 4;
    /**
     * Variables
     */
    private int mType;
    private float mSize;
    private float mRadius;
    private float mMargin;
    private int mProgress;
    /**
     * Bitmap
     */
    private Bitmap mCircleBackground;
    private Bitmap mAlphabet;
    /**
     * Paint
     */
    private Paint mProgressPaint;
    private Paint mTransparentPaint;
    private Paint mAlphaPaint;
    /**
     * Animation
     */
    private Transformation mAlphaTransformation;
    private AlphaAnimation mFadeOut;
    private AlphaAnimation mFadeIn;
    private Transformation mScaleTransformation;
    private AlphaAnimation mScale;

    public Clicker(Context context) {
        this(context, null);
    }

    public Clicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Clicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // load the styled attributes and set their properties
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Clicker, defStyleAttr, 0);
        mType = attributes.getInt(R.styleable.Clicker_alphabet, A);
        mRadius = DipPixelHelper.getPixel(getContext(), SMALL_WHEEL_RADIUS);
        mSize = DipPixelHelper.getPixel(getContext(), SMALL_SIZE);
        mMargin = (mSize - mRadius * 2) / 2.0f;

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.FILL);

        mTransparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentPaint.setStyle(Paint.Style.FILL);
        mTransparentPaint.setColor(getResources().getColor(R.color.bttendance_white));

        switch (mType) {
            case A:
                mAlphabet = BitmapFactory.decodeResource(getResources(), R.drawable.ic_a);
                mProgressPaint.setColor(getResources().getColor(R.color.bttendance_a));
                break;
            case B:
                mAlphabet = BitmapFactory.decodeResource(getResources(), R.drawable.ic_b);
                mProgressPaint.setColor(getResources().getColor(R.color.bttendance_b));
                break;
            case C:
                mAlphabet = BitmapFactory.decodeResource(getResources(), R.drawable.ic_c);
                mProgressPaint.setColor(getResources().getColor(R.color.bttendance_c));
                break;
            case D:
                mAlphabet = BitmapFactory.decodeResource(getResources(), R.drawable.ic_d);
                mProgressPaint.setColor(getResources().getColor(R.color.bttendance_d));
                break;
            case E:
            default:
                mAlphabet = BitmapFactory.decodeResource(getResources(), R.drawable.ic_e);
                mProgressPaint.setColor(getResources().getColor(R.color.bttendance_e));
                break;
        }

        mCircleBackground = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bttendance_circle_cyan_small);

        mAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAlphaTransformation = new Transformation();
        mFadeOut = new AlphaAnimation(1f, 0f);
        mFadeIn = new AlphaAnimation(0f, 1f);
        mFadeOut.setDuration(BLINK_DURATION);
        mFadeIn.setDuration(BLINK_DURATION);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mProgress == 0)
                    return;
                mFadeIn.start();
                mFadeIn.getTransformation(System.currentTimeMillis(), mAlphaTransformation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mProgress == 0)
                    return;
                mFadeOut.start();
                mFadeOut.getTransformation(System.currentTimeMillis(), mAlphaTransformation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mScaleTransformation = new Transformation();
        mScale = new AlphaAnimation(1f, 0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mScale.hasStarted() && !mScale.hasEnded()) {
            canvas.drawCircle(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, mRadius, mProgressPaint);
            mScale.getTransformation(System.currentTimeMillis(), mScaleTransformation);
            mProgress = (int) (100 * mScaleTransformation.getAlpha());
            canvas.drawRect(mMargin, mMargin, mSize - mMargin, mMargin + mRadius * 2 * (100 - mProgress) / 100, mTransparentPaint);
        }

        canvas.drawBitmap(mCircleBackground, 0, 0, null);

        if (mFadeOut.hasStarted() && !mFadeOut.hasEnded()) {
            mFadeOut.getTransformation(System.currentTimeMillis(), mAlphaTransformation);
            mAlphaPaint.setAlpha((int) (255 * mAlphaTransformation.getAlpha()));
            invalidate();
        } else if (mFadeIn.hasStarted() && !mFadeIn.hasEnded()) {
            mFadeIn.getTransformation(System.currentTimeMillis(), mAlphaTransformation);
            mAlphaPaint.setAlpha((int) (255 * mAlphaTransformation.getAlpha()));
            invalidate();
        } else {
            mAlphaPaint.setAlpha(1);
        }
        canvas.drawBitmap(mAlphabet, 0, 0, mAlphaPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mSize, (int) mSize);
    }

    public void startClicker(int progress) {
        mProgress = progress;
        mFadeOut.start();
        mFadeOut.getTransformation(System.currentTimeMillis(), mAlphaTransformation);
        mScale = new AlphaAnimation(1f * (float) progress / 100f, 0f);

        int duration = PROGRESS_DURATION * progress / 100;
        if (duration >= 0)
            mScale.setDuration(duration);
        else
            mScale.setDuration(0);

        mScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (System.currentTimeMillis() > animation.getStartTime() + animation.getDuration()) {
                    mProgress = 0;
                    mFadeIn.cancel();
                    mFadeOut.cancel();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mScale.start();
        mScale.getTransformation(System.currentTimeMillis(), mScaleTransformation);
        invalidate();
    }

    @Override
    public void startAnimation(Animation animation) {
        super.startAnimation(animation);
        clearAnimation();
    }
}
