package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 12/15/14.
 */
public class UserPostRequest {

    public User user;

    public class User {
        public String email;
        public String password;
        public String name;
        public String locale;
        public Preference preferences_attributes;
        public Device[] devices_attributes;
    }

    public class Preference {
        public Boolean clicker;
        public Boolean attendance;
        public Boolean notice;
        public Boolean curious;
    }

    public class Device {
        public String platform;
        public String uuid;
        public String mac_address;
        public String notification_key;
    }
}
