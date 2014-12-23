package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 12/15/14.
 */
public class PasswordResetRequest {

    public User user;

    public PasswordResetRequest(String email) {
        this.user = new User();
        this.user.email = email;
    }

    public class User {
        public String email;
    }
}
