package com.bttendance.event;

/**
 * Bluetooth Divice has been found
 *
 * Created by TheFinestArtist on 2013. 12. 26..
 */
public class BTDiscoveredEvent {
    String mMacAddress;

    public BTDiscoveredEvent(String macAddress) {
        mMacAddress = macAddress;
    }

    public String getMacAddress() {
        return mMacAddress;
    }
}
