package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;

import com.bttendance.adapter.kit.InstantCursorAdapter;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2014. 2. 26..
 */
public class ChooseSchoolAdapter extends InstantCursorAdapter<SchoolJson> {

    /**
     * Constructs a new {@link com.bttendance.adapter.kit.InstantCursorAdapter} backed by your {@link android.database.Cursor}.
     *
     * @param context          The {@link android.content.Context} to use.
     * @param layoutResourceId The resource id of your XML layout.
     * @param dataType         The data type backed by your adapter.
     * @param cursor           The {@link android.database.Cursor} to be used.
     */
    public ChooseSchoolAdapter(Context context, int layoutResourceId, Class<?> dataType, Cursor cursor) {
        super(context, layoutResourceId, dataType, cursor);
    }

    @Override
    public SchoolJson getInstance(Cursor cursor) {
        return BTTable.SchoolTable.get(cursor.getInt(0));
    }

}
