package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 12/31/14.
 */
public class CourseSearchRequest {
    String id;
    String code;

    public CourseSearchRequest(String code) {
        this.code = code;
    }
}
