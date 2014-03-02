package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class ProfileAdapter extends CursorAdapter {

    UserJson user;

    public ProfileAdapter(Context context, Cursor c) {
        super(context, c, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        user = BTPreference.getUser(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.listitem_profile_school, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(0);
        SchoolJson school = BTTable.SchoolTable.get(id);
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(school.name);

        TextView key = (TextView) view.findViewById(R.id.key);
        int position = cursor.getPosition();
        if (position < user.employed_schools.length)
            key.setText(mContext.getString(R.string.professor));
        else
            key.setText(user.enrolled_schools[position - user.employed_schools.length].key);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        user = BTPreference.getUser(mContext);
        return super.swapCursor(newCursor);
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