package com.bttendance.service.request;

import com.bttendance.service.BTAPI;

/**
 * Created by TheFinestArtist on 12/15/14.
 */
public class UserPostRequest {

    public User user;

    public UserPostRequest(String email, String password, String name, String mac_address) {
        this.user = new User();
        this.user.email = email;
        this.user.password = password;
        this.user.name = name;
        this.user.devices_attributes = new Device[]{new Device()};
        this.user.devices_attributes[0].platform = BTAPI.DevicePlatform.android.name();
        this.user.devices_attributes[0].mac_address = mac_address;
    }

    public class User {
        public String email;
        public String password;
        public String name;
        public String locale;
        public Device[] devices_attributes;
    }

    public class Device {
        public String platform;
        public String uuid;
        public String mac_address;
    }
}
