package com.utopia.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class SchoolCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public SchoolCursor() {
        super(COLUMNS);
        SparseArray<SchoolJson> table = BTTable.SchoolTable;
        for (int i = 0; i < table.size(); i++)
            addRow(new Object[]{table.keyAt(i)});
    }
}
