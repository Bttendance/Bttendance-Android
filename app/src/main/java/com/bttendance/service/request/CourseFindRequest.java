package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 12/31/14.
 */
public class CourseFindRequest {
    int id;
    String code;

    public CourseFindRequest(String code) {
        this.code = code;
    }
}
