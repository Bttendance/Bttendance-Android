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
public class Bttendance extends View {

    /**
     * Duration
     */
    public static final int PROGRESS_DURATION = 65000;
    private static final int BLINK_DURATION = 1000;
    /**
     * Dimension
     */
    private static final int BIG_SIZE = 200; //in dp
    private static final int SMALL_SIZE = 52; //in dp
    private static final float BIG_WHEEL_RADIUS = 92.0f; //in dp (135)
    private static final float SMALL_WHEEL_RADIUS = 24.2f; //in dp (52)
    /**
     * Type
     */
    private static final int BIG = 0;
    private static final int SMALL = 1;
    /**
     * Variables
     */
    private int mType;
    private float mSize;
    private float mRadius;
    private float mMargin;
    private STATE mState;
    private int mProgress;
    /**
     * Bitmap
     */
    private Bitmap mAbsentBackground;
    private Bitmap mPresendBackground;
    private Bitmap mTardyBackground;
    private Bitmap mCircleBackground;
    private Bitmap mCyanCheck;
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

    public Bttendance(Context context) {
        this(context, null);
    }

    public Bttendance(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Bttendance(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // load the styled attributes and set their properties
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Bttendance, defStyleAttr, 0);
        mType = attributes.getInt(R.styleable.Bttendance_type, SMALL);
        switch (mType) {
            case BIG:
                mRadius = DipPixelHelper.getPixel(getContext(), BIG_WHEEL_RADIUS);
                mSize = DipPixelHelper.getPixel(getContext(), BIG_SIZE);
                break;
            case SMALL:
            default:
                mRadius = DipPixelHelper.getPixel(getContext(), SMALL_WHEEL_RADIUS);
                mSize = DipPixelHelper.getPixel(getContext(), SMALL_SIZE);
                break;
        }
        mMargin = (mSize - mRadius * 2) / 2.0f;

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(getResources().getColor(R.color.bttendance_navy));

        mTransparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentPaint.setStyle(Paint.Style.FILL);
        mTransparentPaint.setColor(getResources().getColor(R.color.bttendance_white));

        switch (mType) {
            case BIG:
                mCyanCheck = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                break;
            case SMALL:
            default:
                mCyanCheck = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                break;
        }

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
        mState = STATE.PRESENT;
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

        if (mState == STATE.GRADE) {
            canvas.drawCircle(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, mRadius, mProgressPaint);
            canvas.drawRect(mMargin, mMargin, mSize - mMargin, mMargin + mRadius * 2 * (100 - mProgress) / 100, mTransparentPaint);
        }

        Bitmap background = getBackgroundBitmap();
        if (background != null)
            canvas.drawBitmap(background, 0, 0, null);

        Bitmap check = getCheckBitmap();
        if (check != null)
            canvas.drawBitmap(check, 0, 0, null);

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
        canvas.drawBitmap(mCyanCheck, 0, 0, mAlphaPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mSize, (int) mSize);
    }

    private Bitmap getCheckBitmap() {

        switch (mType) {
            case BIG:
                switch (mState) {
                    case GRADE:
                        if (mCyanCheck == null)
                            return mCyanCheck = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mCyanCheck;
                    case CHECKING:
                    case ABSENT:
                    case PRESENT:
                    case TARDY:
                    default:
                        return null;
                }
            case SMALL:
            default:
                switch (mState) {
                    case GRADE:
                        if (mCyanCheck == null)
                            return mCyanCheck = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mCyanCheck;
                    case CHECKING:
                    case ABSENT:
                    case PRESENT:
                    case TARDY:
                    default:
                        return null;
                }
        }
    }

    private Bitmap getBackgroundBitmap() {

        switch (mType) {
            case BIG:
                switch (mState) {
                    case ABSENT:
                        if (mAbsentBackground == null)
                            return mAbsentBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mAbsentBackground;
                    case PRESENT:
                        if (mPresendBackground == null)
                            return mPresendBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mPresendBackground;
                    case TARDY:
                        if (mTardyBackground == null)
                            return mTardyBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mTardyBackground;
                    case CHECKING:
                    case GRADE:
                        if (mCircleBackground == null)
                            return mCircleBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mCircleBackground;
                    default:
                        return null;
                }
            case SMALL:
            default:
                switch (mState) {
                    case ABSENT:
                        if (mAbsentBackground == null)
                            return mAbsentBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mAbsentBackground;
                    case PRESENT:
                        if (mPresendBackground == null)
                            return mPresendBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mPresendBackground;
                    case TARDY:
                        if (mTardyBackground == null)
                            return mTardyBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mTardyBackground;
                    case CHECKING:
                    case GRADE:
                        if (mCircleBackground == null)
                            return mCircleBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clicker_choice_a_default);
                        else
                            return mCircleBackground;
                    default:
                        return null;
                }
        }
    }

    public void setBttendance(STATE state, int progress) {
        mProgress = progress;
        mState = state;
        switch (mState) {
            case CHECKING:
                startBttendance(mProgress);
                break;
            case GRADE:
                endBttendance(state, progress);
                break;
            case TARDY:
            case ABSENT:
            case PRESENT:
                endBttendance(state, 0);
                break;
            default:
                break;
        }
        invalidate();
    }

    private void startBttendance(int progress) {
        mProgress = progress;
        mState = STATE.CHECKING;
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
    }

    private void endBttendance(STATE state, int progress) {
        mState = state;
        mScale.cancel();
        mFadeIn.cancel();
        mFadeOut.cancel();
        mProgress = progress;
    }

    @Override
    public void startAnimation(Animation animation) {
        super.startAnimation(animation);
        clearAnimation();
    }

    public static enum STATE {CHECKING, ABSENT, PRESENT, TARDY, GRADE}
}
