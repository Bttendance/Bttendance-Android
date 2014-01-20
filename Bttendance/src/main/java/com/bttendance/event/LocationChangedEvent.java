package com.bttendance.event;

import android.location.Location;

/**
 * Created by TheFinestArtist on 2013. 12. 27..
 */
public class LocationChangedEvent {

    private Location mLocation;

    public LocationChangedEvent(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }
}
