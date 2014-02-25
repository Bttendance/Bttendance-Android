package com.bttendance.model.list;

import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseList extends ArrayList<CourseJson> {

    public CourseList(String filter) {
        SparseArray<CourseJson> table = BTTable.getCourses(filter);
        if (table == null)
            return;

        List<CourseJson> arrayList = new ArrayList<CourseJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }

    public CourseList(SparseArray<CourseJson> table) {
        if (table == null)
            return;

        List<CourseJson> arrayList = new ArrayList<CourseJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }
}
