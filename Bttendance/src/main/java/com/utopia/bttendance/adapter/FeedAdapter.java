package com.utopia.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.utopia.bttendance.R;
import com.utopia.bttendance.helper.DateHelper;
import com.utopia.bttendance.helper.IntArrayHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.view.Bttendance;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class FeedAdapter extends CursorAdapter {

    public FeedAdapter(Context context, Cursor c) {
        super(context, c, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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

        long currentTime = DateHelper.getCurrentGMTTimeMillis();

        boolean mTime = currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION;
        boolean included = IntArrayHelper.contains(post.checks, BTPreference.getUserId(context));

        if (mTime && !included) {
            long time = currentTime - DateHelper.getTime(post.createdAt);
            int progress = (int) ((float) 100 * ((float) Bttendance.PROGRESS_DURATION - (float) time) / (float) Bttendance.PROGRESS_DURATION);
            bttendance.setEndState(Bttendance.STATE.FAIL);
            bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
        } else if (mTime || included) {
            bttendance.setBttendance(Bttendance.STATE.CHECKED, 0);
        } else {
            bttendance.setBttendance(Bttendance.STATE.FAIL, 0);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(post.title);
        message.setText(post.message + "\n" + DateHelper.getBTFormatString(post.createdAt));
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
