package com.bttendance.model.cursor;

import android.content.Context;
import android.database.MatrixCursor;
import android.util.SparseArray;

import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class MyCourseCursor extends MatrixCursor {

    public static final int ADD_BUTTON_CREATE_COURSE = -2;
    public static final int ADD_BUTTON_ATTEND_COURSE = -3;

    private final static String[] COLUMNS = {
            "_id"
    };

    public MyCourseCursor(Context context) {
        super(COLUMNS);
        UserJson user = BTPreference.getUser(context);
        SparseArray<CourseJson> table = BTTable.getCourses(BTTable.FILTER_MY_COURSE);

        for (int i = 0; i < user.supervising_courses.length; i++)
            if (table.get(user.supervising_courses[i]) != null)
                addRow(new Object[]{user.supervising_courses[i]});

        addRow(new Object[]{ADD_BUTTON_CREATE_COURSE});

        for (int i = 0; i < user.attending_courses.length; i++)
            if (table.get(user.attending_courses[i]) != null)
                addRow(new Object[]{user.attending_courses[i]});

        addRow(new Object[]{ADD_BUTTON_ATTEND_COURSE});
    }
}
