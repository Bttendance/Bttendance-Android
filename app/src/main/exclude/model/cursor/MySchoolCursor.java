package com.bttendance.model.cursor;

import android.content.Context;
import android.database.MatrixCursor;

import com.bttendance.model.BTPreference;
import com.bttendance.model.json.SchoolJsonSimple;
import com.bttendance.model.json.UserJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
        if (user == null)
            return;

        ArrayList<SchoolJsonSimple> schools = new ArrayList<SchoolJsonSimple>();
        schools.addAll(Arrays.asList(user.employed_schools));
        schools.addAll(Arrays.asList(user.enrolled_schools));

        Collections.sort(schools, new Comparator<SchoolJsonSimple>() {
            @Override
            public int compare(SchoolJsonSimple lhs, SchoolJsonSimple rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });

        for (SchoolJsonSimple school : schools)
            addRow(new Object[]{school.id});
    }
}
