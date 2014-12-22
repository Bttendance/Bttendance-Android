package com.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.json.QuestionJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class QuestionCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public QuestionCursor(SparseArray<QuestionJson> table) {
        super(COLUMNS);
        for (int i = table.size() - 1; i >= 0; i--)
            addRow(new Object[]{table.keyAt(i)});
    }
}
