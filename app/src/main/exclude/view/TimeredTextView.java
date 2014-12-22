package com.bttendance.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.event.update.UpdateFeedEvent;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.PostJson;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2014. 8. 25..
 */
public class TimeredTextView extends TextView {

    public enum Type {Clicker, Attendance, Notice}

    private Type mType = null;
    private PostJson mPost = null;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (mPost == null || mType == null)
                return;

            int progressDuration = Bttendance.PROGRESS_DURATION;
            if (mType == Type.Clicker)
                progressDuration = (mPost.clicker.progress_time + 5) * 1000;

            long leftTime = progressDuration - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt);
            if (leftTime > progressDuration - 5000)
                leftTime = progressDuration - 5000;

            if (leftTime < 0) {
                timerHandler.removeCallbacks(timerRunnable);
                // Timer End
                BTEventBus.getInstance().post(new UpdateFeedEvent());
            } else {
                String message = null;
                switch (mType) {
                    case Clicker:
                        message = String.format(getContext().getString(R.string.clicker_timered_message), (int) leftTime / 1000);
                        break;
                    case Attendance:
                        message = String.format(getContext().getString(R.string.attendance_timered_message), (int) leftTime / 1000);
                        break;
                }
                setText(message);
                timerHandler.postDelayed(this, 200);
            }
        }
    };

    public TimeredTextView(Context context) {
        super(context);
    }

    public TimeredTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTimeredTextView(Type type, int postID, int userID, boolean auth) {
        mType = type;
        mPost = BTTable.PostTable.get(postID);

        if (mPost == null)
            return;

        timerHandler.removeCallbacks(timerRunnable);
        switch (mType) {
            case Clicker:
                if ((mPost.clicker.progress_time + 5) * 1000 - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0)
                    timerHandler.postDelayed(timerRunnable, 0);
                break;
            case Attendance:
                if (AttendanceJson.TYPE_AUTO.equals(mPost.attendance.type)
                        && (auth || mPost.attendance.getStateInt(userID) == 0)
                        && Bttendance.PROGRESS_DURATION - System.currentTimeMillis() + DateHelper.getTime(mPost.createdAt) > 0)
                    timerHandler.postDelayed(timerRunnable, 0);
                break;
            case Notice:
                break;
        }
    }

    public Type getType() {
        return mType;
    }

    public PostJson getPost() {
        return mPost;
    }

}
