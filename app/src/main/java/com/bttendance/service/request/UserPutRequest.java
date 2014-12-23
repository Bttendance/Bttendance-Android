package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 12/23/14.
 */
public class UserPutRequest {

    public User user;

    public class User {
        public String email;
        public String password;
        public String new_password;
        public String name;
        public String locale;
        public Preferences preferences_attributes;
        public Device[] devices_attributes;
        public School[] schools_users_attributes;
        public Course[] courses_users_attributes;
    }

    public class Preferences {
        public boolean clicker;
        public boolean attendance;
        public boolean notice;
        public boolean curious;
    }

    public class Device {
        public int id;
        public String platform;
        public String uuid;
        public String mac_address;
        public String notification_key;
        public boolean _destroy;
    }

    public class School {
        public int school_id;
        public String identity;
        public String state;
        public boolean _destroy;
    }

    public class Course {
        public int course_id;
        public String state;
        public boolean _destroy;
    }
}