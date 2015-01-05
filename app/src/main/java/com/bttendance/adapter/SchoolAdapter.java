package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;

import com.bttendance.adapter.kit.InstantCursorAdapter;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2014. 2. 26..
 */
public class SchoolAdapter extends InstantCursorAdapter<SchoolJson> {

    public SchoolAdapter(Context context, int layoutResourceId, Class<?> dataType, Cursor cursor) {
        super(context, layoutResourceId, dataType, cursor);
    }

    @Override
    public SchoolJson getInstance(Cursor cursor) {
        return BTTable.SchoolTable.get(cursor.getInt(0));
    }

}
