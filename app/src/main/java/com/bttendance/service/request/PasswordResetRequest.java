package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 12/15/14.
 */
public class PasswordResetRequest {

    public User user;

    public class User {
        public String email;

        public User(String email) {
            this.email = email;
        }
    }

    public PasswordResetRequest(String email) {
        this.user = new User(email);
    }
}
