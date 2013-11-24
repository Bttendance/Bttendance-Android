package com.utopia.bttendance.activity.sign;

import android.os.Bundle;
import android.widget.SeekBar;

import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class PersionalizeActivity extends BTActivity {

    private static final int PROGRESS_TO_GO = 80;
    private static final int PROGRESS_CHANGE_MAX = 30;
    private static final int PROGRESS_CHANGE_DURATION = 2;
    private static final int SPEED_TO_GO_LEFT = 13; //Std
    private static final int SPEED_TO_GO_RIGHT = 11; //Prof
    SeekBar mSeekBarStd;
    int progressStd = 0;
    int progressStdStart = 0;
    boolean isProgressStdChanging = false;
    boolean isProgressStdChangingStarted = false;
    boolean isProgressStdSpeedToGo = false;
    Thread threadStd;
    SeekBar mSeekBarProf;
    int progressProf = 100;
    int progressProfStart = 100;
    boolean isProgressProfChanging = false;
    boolean isProgressProfChangingStarted = false;
    boolean isProgressProfSpeedToGo = false;
    Thread threadProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_personalize);

        mSeekBarStd = (SeekBar) findViewById(R.id.seekbar_std);
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
                    if (seekBar.getProgress() - progressStd > SPEED_TO_GO_LEFT)
                        isProgressStdSpeedToGo = true;
                    progressStd = progress;
                } else {
                    seekBar.setProgress(progressStd);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressStdStart = seekBar.getProgress();
                isProgressStdChanging = false;
                isProgressStdChangingStarted = false;
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (isProgressStdSpeedToGo || progress > PROGRESS_TO_GO) {
                    if (threadStd != null) {
                        threadStd.interrupt();
                        threadStd = null;
                    }
                    threadStd = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (seekBar.getProgress() != 100) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() + 1);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
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
                            while (seekBar.getProgress() != 0) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() - 1);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    threadStd.start();
                }
            }
        });

        mSeekBarProf = (SeekBar) findViewById(R.id.seekbar_prof);
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
                    if (progressProf - seekBar.getProgress() > SPEED_TO_GO_RIGHT)
                        isProgressProfSpeedToGo = true;
                    progressProf = progress;
                } else {
                    seekBar.setProgress(progressProf);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressProfStart = seekBar.getProgress();
                isProgressProfChanging = false;
                isProgressProfChangingStarted = false;
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (isProgressProfSpeedToGo || progress < 100 - PROGRESS_TO_GO) {
                    if (threadProf != null) {
                        threadProf.interrupt();
                        threadProf = null;
                    }
                    threadProf = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (seekBar.getProgress() != 0) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() - 1);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
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
                            while (seekBar.getProgress() != 100) {
                                try {
                                    Thread.sleep(PROGRESS_CHANGE_DURATION);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(seekBar.getProgress() + 1);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    threadProf.start();
                }
            }
        });
    }
}
