package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 12/22/14.
 */

public class ErrorJson extends BTJson {

    public enum Type {log, toast, alert}

    public String type;
    public String title;
    public String message;
}
