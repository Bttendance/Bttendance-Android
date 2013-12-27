package com.utopia.bttendance.event;

/**
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
