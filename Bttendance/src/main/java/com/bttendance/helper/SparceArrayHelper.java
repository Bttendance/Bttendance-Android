package com.bttendance.helper;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class SparceArrayHelper {

    public static <C> ArrayList<C> asArrayList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return new ArrayList<C>();
        ArrayList<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }
}
