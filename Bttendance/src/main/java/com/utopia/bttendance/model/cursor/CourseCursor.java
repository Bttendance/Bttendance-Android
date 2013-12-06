package com.utopia.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.utopia.bttendance.adapter.CourseAdapter;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.PostJson;

import java.util.ArrayList;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public CourseCursor(String filter) {
        super(COLUMNS);
        ArrayList<CourseJson> table = BTTable.getCourses(filter);
        for (int i = 0; i < table.size(); i++)
            addRow(new Object[]{table.get(i)});
    }
}
