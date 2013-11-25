package com.utopia.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class ErrorsJson {
    public String errors;
    public String message;
    public String alert;
    public String toast;
    public String uuid;

    @Override
    public String toString() {
        return errors;
    }

    public String getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getAlert() {
        return alert;
    }

    public String getToast() {
        return toast;
    }

    public String getUUID() {
        return uuid;
    }
}
