package com.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class SchoolCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public SchoolCursor(String filter) {
        super(COLUMNS);
        SparseArray<SchoolJson> table = BTTable.getSchools(filter);
        for (int i = table.size() - 1; i >= 0; i--)
            addRow(new Object[]{table.keyAt(i)});
    }
}
