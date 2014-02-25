package com.bttendance.model.list;

import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class SchoolList extends ArrayList<SchoolJson> {

    public SchoolList(String filter) {
        SparseArray<SchoolJson> table = BTTable.getSchools(filter);
        if (table == null)
            return;

        List<SchoolJson> arrayList = new ArrayList<SchoolJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }

    public SchoolList(SparseArray<SchoolJson> table) {
        if (table == null)
            return;

        List<SchoolJson> arrayList = new ArrayList<SchoolJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }
}
