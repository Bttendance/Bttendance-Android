package com.bttendance.service.request;

import com.bttendance.service.BTAPI;

/**
 * Created by TheFinestArtist on 12/15/14.
 */
public class LogInRequest {

    public User user;

    public LogInRequest(String email, String password, String mac_address) {
        this.user = new User();
        this.user.email = email;
        this.user.password = password;
        this.user.devices_attributes = new Device();
        this.user.devices_attributes.platform = BTAPI.ANDROID;
        this.user.devices_attributes.mac_address = mac_address;
    }

    public class User {
        public String email;
        public String password;
        public String locale;
        public Device devices_attributes;
    }

    public class Device {
        public String platform;
        public String uuid;
        public String mac_address;
    }
}
