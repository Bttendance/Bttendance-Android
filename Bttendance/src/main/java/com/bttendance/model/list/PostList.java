package com.bttendance.model.list;

import android.util.SparseArray;

import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class PostList extends ArrayList<PostJson> {

    public PostList(String filter) {
        SparseArray<PostJson> table = BTTable.getPosts(filter);
        if (table == null)
            return;

        List<PostJson> arrayList = new ArrayList<PostJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }

    public PostList(SparseArray<PostJson> table) {
        if (table == null)
            return;

        List<PostJson> arrayList = new ArrayList<PostJson>(table.size());
        for (int i = 0; i < table.size(); i++)
            add(table.valueAt(i));
    }
}
