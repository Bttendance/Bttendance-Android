package com.bttendance.helper;

import com.bttendance.model.json.BTJsonSimple;

/**
 * Created by TheFinestArtist on 2013. 12. 25..
 */
public class IntArrayHelper {

    public static boolean contains(int[] array, int value) {
        if (array == null)
            return false;

        for (int i : array)
            if (i == value)
                return true;
        return false;
    }

    public static boolean contains(BTJsonSimple[] array, int value) {
        if (array == null)
            return false;

        for (BTJsonSimple json : array)
            if (json.id == value)
                return true;
        return false;
    }

    public static int[] add(int[] array, int value) {
        if (array == null)
            return new int[]{value};

        int[] newArray = new int[array.length + 1];
        for (int i = 0; i < array.length; i++)
            newArray[i] = array[i];
        newArray[array.length] = value;

        return newArray;
    }

}
