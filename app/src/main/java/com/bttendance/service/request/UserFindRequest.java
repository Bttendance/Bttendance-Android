package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 1/2/15.
 */
public class UserFindRequest {
    int id;
    String email;

    public UserFindRequest(String email) {
        this.email = email;
    }
}
