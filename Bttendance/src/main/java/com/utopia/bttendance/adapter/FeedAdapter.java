package com.utopia.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.utopia.bttendance.R;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.view.Bttendance;

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
        ((Bttendance) view.findViewById(R.id.bttendance)).setBttendance(Bttendance.STATE.STARTED, 100);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        int id = cursor.getInt(0);
        PostJson post = BTTable.PostTable.get(id);
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
