package com.bttendance.activity.sign;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ViewSwitcher;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.helper.ScreenHelper;
import com.bttendance.model.BTKey;
import com.bttendance.view.Circle;
import com.bttendance.view.SeekBar_;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class PersionalizeActivity extends BTActivity {

    private static final int PROGRESS_TO_GO = 80;
    private static final int PROGRESS_CHANGE_MAX = 30;
    private static final int PROGRESS_CHANGE_DURATION = 6;
    private static final int PROGRESS_CHANGE = 3;
    private static final int SPEED_TO_GO = 14;
    SeekBar_ mSeekBarStd;
    int progressStd = 100;
    int progressStdStart = 100;
    boolean isProgressStdChanging = false;
    boolean isProgressStdChangingStarted = false;
    boolean isProgressStdSpeedToGo = false;
    Thread threadStd;
    Circle mStdCircle;
    SeekBar_ mSeekBarProf;
    int progressProf = 0;
    int progressProfStart = 0;
    boolean isProgressProfChanging = false;
    boolean isProgressProfChangingStarted = false;
    boolean isProgressProfSpeedToGo = false;
    Thread threadProf;
    Circle mProfCircle;
    int statusBarHeight;
    int actionBarHeight;
    BTKey.Type mType = BTKey.Type.NULL;
    RelativeLayout mUnderLayout;
    RelativeLayout mTextLayout;
    RelativeLayout mProfLayout;
    RelativeLayout mStdLayout;
    ViewSwitcher mViewSwitcher;

    private void initCircle(Circle circle, SeekBar seekBar) {
        if (((SeekBar_) seekBar).getSeekBarThumb() == null)
            return;

        Rect thumbRect = ((SeekBar_) seekBar).getSeekBarThumb().getBounds();
        int[] location = new int[2];
        seekBar.getLocationOnScreen(location);
        int x = location[0] + thumbRect.centerX();
        int y = location[1];
        circle.initView(x, y, seekBar.getProgress());
    }

    private void initText() {
        int paddingBottom = (int) (DipPixelHelper.getPixel(this, 50) + mSeekBarProf.getHeight() / 2);
        mProfLayout.setPadding(0, 0, 0, paddingBottom);
        mStdLayout.setPadding(0, 0, 0, paddingBottom);
        mTextLayout.setPadding(0, 0, 0, paddingBottom);
    }

    private void updateCircle(Circle circle, SeekBar seekBar) {
        if (((SeekBar_) seekBar).getSeekBarThumb() == null)
            return;

        Rect thumbRect = ((SeekBar_) seekBar).getSeekBarThumb().getBounds();
        int[] location = new int[2];
        seekBar.getLocationOnScreen(location);
        int x = location[0] + thumbRect.centerX();
        int y = location[1];
        circle.updateView(x, y, seekBar.getProgress());
    }

    private void nextActivity(BTKey.Type type) {
        switch (type) {
            case PROFESSOR:
                mType = BTKey.Type.PROFESSOR;
                Intent intent_prof = new Intent(PersionalizeActivity.this, ProfessorSerial.class);
                startActivity(intent_prof);
                break;
            case STUDENT:
            default:
                mType = BTKey.Type.STUDENT;
                Intent intent_std = new Intent(PersionalizeActivity.this, StudentConfirm.class);
                startActivity(intent_std);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        switch (mType) {
            case PROFESSOR:
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
                break;
            case STUDENT:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            default:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(6000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mViewSwitcher.getDisplayedChild() == 0) {
                                    mViewSwitcher.showNext();
                                } else {
                                    mViewSwitcher.showPrevious();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        mUnderLayout = (RelativeLayout) findViewById(R.id.under_layout);
        mTextLayout = (RelativeLayout) findViewById(R.id.text_layout);
        mProfLayout = (RelativeLayout) findViewById(R.id.prof_layout);
        mStdLayout = (RelativeLayout) findViewById(R.id.std_layout);

        mSeekBarStd = (SeekBar_) findViewById(R.id.seekbar_std);
        mSeekBarStd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
                if (!isProgressStdChangingStarted && Math.abs(progress - progressStdStart) <= PROGRESS_CHANGE_MAX)
                    isProgressStdChanging = true;
                isProgressStdChangingStarted = true;
                if (isProgressStdChanging) {
                    if (threadStd != null) {
                        threadStd.interrupt();
                        threadStd = null;
                    }
                    if (progressStd - seekBar.getProgress() > SPEED_TO_GO)
                        isProgressStdSpeedToGo = true;
                    if (seekBar.getProgress() - progressStd > 0)
                        isProgressStdSpeedToGo = false;
                    progressStd = progress;
                    updateCircle(mStdCircle, seekBar);
                } else {
                    seekBar.setProgress(progressStd);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProfLayout.setVisibility(View.INVISIBLE);
                if (threadStd != null) {
                    threadStd.interrupt();
                    threadStd = null;
                }
                progressStdStart = seekBar.getProgress();
                isProgressStdChanging = false;
                isProgressStdChangingStarted = false;
                seekBar.bringToFront();
                mStdCircle.bringToFront();
                sendViewToBack(mProfCircle);
                sendViewToBack(mSeekBarProf);
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (isProgressStdSpeedToGo || progress < 100 - PROGRESS_TO_GO) {
                    threadStd = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (seekBar.getProgress() > 0) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() - PROGRESS_CHANGE);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            nextActivity(BTKey.Type.STUDENT);
                            isProgressStdSpeedToGo = false;
                        }
                    });
                    threadStd.start();
                } else {
                    if (threadStd != null) {
                        threadStd.interrupt();
                        threadStd = null;
                    }
                    threadStd = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (seekBar.getProgress() < 100) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() + PROGRESS_CHANGE);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProfLayout.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                    threadStd.start();
                }
            }
        });

        mSeekBarProf = (SeekBar_) findViewById(R.id.seekbar_prof);
        mSeekBarProf.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
                if (!isProgressProfChangingStarted && Math.abs(progress - progressProfStart) <= PROGRESS_CHANGE_MAX)
                    isProgressProfChanging = true;
                isProgressProfChangingStarted = true;
                if (isProgressProfChanging) {
                    if (threadProf != null) {
                        threadProf.interrupt();
                        threadProf = null;
                    }
                    if (seekBar.getProgress() - progressProf > SPEED_TO_GO)
                        isProgressProfSpeedToGo = true;
                    if (progressProf - seekBar.getProgress() > 0)
                        isProgressProfSpeedToGo = false;
                    progressProf = progress;
                    updateCircle(mProfCircle, seekBar);
                } else {
                    seekBar.setProgress(progressProf);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStdLayout.setVisibility(View.INVISIBLE);
                if (threadProf != null) {
                    threadProf.interrupt();
                    threadProf = null;
                }
                progressProfStart = seekBar.getProgress();
                isProgressProfChanging = false;
                isProgressProfChangingStarted = false;
                seekBar.bringToFront();
                mProfCircle.bringToFront();
                sendViewToBack(mStdCircle);
                sendViewToBack(mSeekBarStd);
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (isProgressProfSpeedToGo || progress > PROGRESS_TO_GO) {
                    if (threadProf != null) {
                        threadProf.interrupt();
                        threadProf = null;
                    }
                    threadProf = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (seekBar.getProgress() < 100) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() + PROGRESS_CHANGE);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            nextActivity(BTKey.Type.PROFESSOR);
                            isProgressProfSpeedToGo = false;
                        }
                    });
                    threadProf.start();
                } else {
                    if (threadProf != null) {
                        threadProf.interrupt();
                        threadProf = null;
                    }
                    threadProf = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (seekBar.getProgress() > 0) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() - PROGRESS_CHANGE);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStdLayout.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                    threadProf.start();
                }
            }
        });

        statusBarHeight = ScreenHelper.getSBHeight(this);
        actionBarHeight = ScreenHelper.getABHeight(this);

        mStdCircle = new Circle(this, BTKey.Type.STUDENT);
        mUnderLayout.addView(mStdCircle, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mProfCircle = new Circle(this, BTKey.Type.PROFESSOR);
        mUnderLayout.addView(mProfCircle, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        int[] location1 = new int[2];
                        mSeekBarStd.getLocationOnScreen(location1);
                        int[] location2 = new int[2];
                        mSeekBarProf.getLocationOnScreen(location2);
                        if (location1[1] > 0
                                && location2[1] > 0
                                && mProfLayout.getHeight() > 0
                                && mStdLayout.getHeight() > 0
                                && mTextLayout.getHeight() > 0)
                            break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initCircle(mProfCircle, mSeekBarProf);
                        initCircle(mStdCircle, mSeekBarStd);
                        initText();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSeekBarProf.setProgress(0);
        mSeekBarStd.setProgress(100);
        updateCircle(mStdCircle, mSeekBarStd);
        updateCircle(mProfCircle, mSeekBarProf);
        mProfLayout.setVisibility(View.VISIBLE);
        mStdLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mType = BTKey.Type.NULL;
    }

}