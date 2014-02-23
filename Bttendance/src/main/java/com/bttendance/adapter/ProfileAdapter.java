package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class ProfileAdapter extends CursorAdapter implements View.OnClickListener {

    public ProfileAdapter(Context context, Cursor c) {
        super(context, c, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.listview_entry_item2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(0);
        SchoolJson school = BTTable.SchoolTable.get(id);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(school.name);
        message.setText(school.website);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null)
            return -1;

        int id = cursor.getInt(0);
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                break;
            default:
                break;
        }
    }
}
