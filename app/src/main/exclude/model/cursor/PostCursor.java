package com.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.json.PostJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class PostCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public PostCursor(SparseArray<PostJson> table) {
        super(COLUMNS);
        for (int i = table.size() - 1; i >= 0; i--)
            addRow(new Object[]{table.keyAt(i)});

        addRow(new Object[]{-1});
        addRow(new Object[]{-2});
        addRow(new Object[]{-3});
        addRow(new Object[]{-4});
    }
}
