package com.bttendance.model.list;

import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.UserJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class UserList extends ArrayList<UserJson> {

    public UserList(String filter) {
        SparseArray<UserJson> table = BTTable.getUsers(filter);
        if (table == null)
            return;

        List<UserJson> arrayList = new ArrayList<UserJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }

    public UserList(SparseArray<UserJson> table) {
        if (table == null)
            return;

        List<UserJson> arrayList = new ArrayList<UserJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }
}
