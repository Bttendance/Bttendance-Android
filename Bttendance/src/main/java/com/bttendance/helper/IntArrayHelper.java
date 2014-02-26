package com.bttendance.helper;

import com.bttendance.model.json.UserJson;

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

    public static boolean contains(UserJson.UserSchool[] array, int value) {
        if (array == null)
            return false;

        for (UserJson.UserSchool userSchool : array)
            if (userSchool.id == value)
                return true;
        return false;
    }

}
