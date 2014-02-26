package com.bttendance.model.cursor;

import android.content.Context;
import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class SectionedSchoolCursor extends MatrixCursor {

    private final static String[] COLUMNS = {
            "_id"
    };

    public SectionedSchoolCursor(Context context, int type) {
        super(COLUMNS);
        UserJson user = BTPreference.getUser(context);
        SparseArray<SchoolJson> table = BTTable.SchoolTable;

        switch (type) {
            case MyCourseCursor.ADD_BUTTON_CREATE_COURSE:
                for (int i = 0; i < user.employed_schools.length; i++)
                    if (table.get(user.employed_schools[i].id) != null)
                        addRow(new Object[]{user.employed_schools[i].id});

                for (int i = 0; i < table.size(); i++)
                    if (!IntArrayHelper.contains(user.employed_schools, table.valueAt(i).id))
                        addRow(new Object[]{table.valueAt(i).id});
                break;
            case MyCourseCursor.ADD_BUTTON_ATTEND_COURSE:
            default:
                for (int i = 0; i < user.enrolled_schools.length; i++)
                    if (table.get(user.enrolled_schools[i].id) != null)
                        addRow(new Object[]{user.enrolled_schools[i].id});

                for (int i = 0; i < table.size(); i++)
                    if (!IntArrayHelper.contains(user.enrolled_schools, table.valueAt(i).id))
                        addRow(new Object[]{table.valueAt(i).id});
                break;
        }

    }
}
