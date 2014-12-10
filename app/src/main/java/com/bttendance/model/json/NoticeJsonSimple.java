package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class NoticeJsonSimple extends BTJson {

    public int[] seen_students;
    public int post;

    public boolean seen(int userID) {
        for (int id : seen_students)
            if (id == userID)
                return true;
        return false;
    }
}
