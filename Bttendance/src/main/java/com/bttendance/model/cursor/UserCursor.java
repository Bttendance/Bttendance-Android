package com.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class UserCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public UserCursor(String filter) {
        super(COLUMNS);
        SparseArray<UserJson> table = BTTable.getUsers(filter);
        for (int i = 0; i < table.size(); i++)
            addRow(new Object[]{table.keyAt(i)});
    }
}
