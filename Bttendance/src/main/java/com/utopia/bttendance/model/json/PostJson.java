package com.utopia.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class PostJson {

    public int id;
    public String title;
    public String message;
    public boolean on_going;
    public int author;
    public int course;
    public int[] checks;

    public int getId() {
        return id;
    }

}
