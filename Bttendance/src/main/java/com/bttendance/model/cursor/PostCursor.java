package com.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class PostCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public PostCursor(String filter) {
        super(COLUMNS);
        SparseArray<PostJson> table = BTTable.getPosts(filter);
        for (int i = table.size() - 1; i >= 0; i--)
            addRow(new Object[]{table.keyAt(i)});
    }
}
