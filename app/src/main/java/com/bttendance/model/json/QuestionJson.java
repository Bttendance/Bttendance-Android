package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2014. 5. 11..
 */

public class QuestionJson extends BTJson {

    public String message;
    public int choice_count;
    public int progress_time;
    public boolean show_info_on_select;
    public String detail_privacy;
    public UserJsonSimple owner;

}