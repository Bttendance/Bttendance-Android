package com.utopia.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.helper.DateHelper;
import com.utopia.bttendance.helper.IntArrayHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.view.Bttendance;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class FeedAdapter extends CursorAdapter {

    public FeedAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.feed_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(0);
        PostJson post = BTTable.PostTable.get(id);

        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        bttendance.setTag(R.id.post_id, post.id);
        bttendance.setClickable(false);

        int duration = Bttendance.PROGRESS_DURATION_STD;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long currentTime = cal.getTimeInMillis() - 9 * 60 * 60 * 1000;

        if (currentTime - DateHelper.getTime(post.createdAt) < duration
                && IntArrayHelper.contains(post.checks, BTPreference.getUserId(context))) {
            long time = currentTime - DateHelper.getTime(post.createdAt);
            int progress = (int) (100 * (duration - time) / duration);
            bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
        } else {
            bttendance.setBttendance(Bttendance.STATE.CHECKED, 0);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(post.title);
        message.setText(post.message);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null)
            return -1;

        int id = cursor.getInt(0);
        return id;
    }
}
