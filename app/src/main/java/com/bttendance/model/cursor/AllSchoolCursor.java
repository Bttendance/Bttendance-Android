package com.bttendance.model.cursor;

import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.helper.SparseArrayHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class AllSchoolCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public AllSchoolCursor(String filter) {
        super(COLUMNS);
        UserJson user = BTTable.getMe();
        SparseArray<SchoolJson> schoolJsonSparseArray = BTTable.SchoolTable;
        if (filter == null)
            filter = "";

        ArrayList<SchoolJson> schoolJsonArrayList = SparseArrayHelper.asArrayList(schoolJsonSparseArray);
        if (schoolJsonArrayList == null)
            return;

        Collections.sort(schoolJsonArrayList, new Comparator<SchoolJson>() {
            @Override
            public int compare(SchoolJson lhs, SchoolJson rhs) {
                return rhs.courses_count - lhs.courses_count;
            }
        });

        for (SchoolJson school : schoolJsonArrayList)
            if (school.name.toLowerCase().contains(filter.toLowerCase())
                    && user.hasSchool(school.id))
                addRow(new Object[]{school.id});

        for (SchoolJson school : schoolJsonArrayList)
            if (school.name.toLowerCase().contains(filter.toLowerCase())
                    && !user.hasSchool(school.id))
                addRow(new Object[]{school.id});
    }
}
