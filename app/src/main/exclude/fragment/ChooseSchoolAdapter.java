package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2014. 2. 26..
 */
public class ChooseSchoolAdapter extends InstantCursorAdapter<SchoolJson> {

    public ChooseSchoolAdapter(Context context, int layoutResourceId, Class<?> dataType, Cursor cursor) {
        super(context, layoutResourceId, dataType, cursor);
    }

    @Override
    public SchoolJson getInstance(Cursor cursor) {
        return BTTable.AllSchoolTable.get(cursor.getInt(0));
    }

}
