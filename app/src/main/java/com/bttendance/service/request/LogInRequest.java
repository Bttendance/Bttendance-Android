package com.bttendance.service.request;

import com.bttendance.service.BTAPI;

/**
 * Created by TheFinestArtist on 12/15/14.
 */
public class LogInRequest {

    public String email;
    public String password;
    public String locale;
    public Device devices_attributes;

    public LogInRequest(String email, String password, String mac_address) {
        this.email = email;
        this.password = password;
        this.devices_attributes = new Device();
        this.devices_attributes.platform = BTAPI.DevicePlatform.android.name();
        this.devices_attributes.mac_address = mac_address;
    }

    public class Device {
        public String platform;
        public String uuid;
        public String mac_address;
    }
}
