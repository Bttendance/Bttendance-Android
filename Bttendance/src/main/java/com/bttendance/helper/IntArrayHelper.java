package com.bttendance.helper;

/**
 * Created by TheFinestArtist on 2013. 12. 25..
 */
public class IntArrayHelper {

    public static boolean contains(int[] array, int value) {
        for (int i : array)
            if (i == value)
                return true;
        return false;
    }

}
