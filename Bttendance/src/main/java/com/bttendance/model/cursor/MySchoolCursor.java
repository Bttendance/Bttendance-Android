package com.bttendance.model.cursor;

import android.content.Context;
import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class MySchoolCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public MySchoolCursor(Context context) {
        super(COLUMNS);
        UserJson user = BTPreference.getUser(context);
        SparseArray<SchoolJson> table = BTTable.getSchools(BTTable.FILTER_MY_SCHOOL);

        for (int i = 0; i < user.employed_schools.length; i++)
            if (table.get(user.employed_schools[i].id) != null)
                addRow(new Object[]{user.employed_schools[i].id});

        for (int i = 0; i < user.enrolled_schools.length; i++)
            if (table.get(user.enrolled_schools[i].id) != null)
                addRow(new Object[]{user.enrolled_schools[i].id});
    }
}
