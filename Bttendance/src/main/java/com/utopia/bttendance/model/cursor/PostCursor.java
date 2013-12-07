package com.utopia.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.UserJson;

import java.util.ArrayList;

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
        for (int i = 0; i < table.size(); i++)
            addRow(new Object[]{table.keyAt(i)});
    }
}
