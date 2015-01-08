package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 1/2/15.
 */
public class UserSearchRequest {
    String id;
    String email;

    public UserSearchRequest(String email) {
        this.email = email;
    }
}
