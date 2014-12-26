package com.bttendance.service.request;

import com.bttendance.service.BTAPI;

/**
 * Created by TheFinestArtist on 12/23/14.
 */
public class UserPutRequest {

    public User user;

    public UserPutRequest() {
        this.user = new User();
    }

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

    /**
     * Public Request Create
     */
    public UserPutRequest updateEmail(String email) {
        this.user.email = email;
        return this;
    }

    public UserPutRequest updateName(String name) {
        this.user.name = name;
        return this;
    }

    public UserPutRequest updatePassword(String oldPass, String newPass) {
        this.user.password = oldPass;
        this.user.new_password = newPass;
        return this;
    }

    public UserPutRequest updateNotificationKey(String macAddress, String notiKey) {
        this.user.devices_attributes = new Device[]{new Device()};
        this.user.devices_attributes[0].platform = BTAPI.DevicePlatform.android.name();
        this.user.devices_attributes[0].mac_address = macAddress;
        this.user.devices_attributes[0].notification_key = notiKey;
        return this;
    }

    public UserPutRequest updateIdentity(int schoolId, String identity) {
        this.user.schools_users_attributes = new School[]{new School()};
        this.user.schools_users_attributes[0].school_id = schoolId;
        this.user.schools_users_attributes[0].identity = identity;
        return this;
    }

}