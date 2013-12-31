
package com.utopia.bttendance.helper;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.event.LocationChangedEvent;

public final class GPSTracker {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 10 * 1000; // 20 second
    private final Context mContext;
    // flag for GPS status
    public boolean isGPSEnabled = false;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location mLocation; // mLocation
    double mLatitude; // mLatitude
    double mLongitude; // mLongitude

    LocationListener mNetworkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
            BTDebug.LogError("location : " + mLatitude + " : " + mLongitude);
            BTEventBus.getInstance().post(new LocationChangedEvent(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    LocationListener mGPSListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
            BTDebug.LogError("location : " + mLatitude + " : " + mLongitude);
            BTEventBus.getInstance().post(new LocationChangedEvent(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public GPSTracker(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        updateLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public static boolean isGpsEnable(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gpsEnabled;
    }

    public static boolean isNetworkEnable(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return networkEnabled;
    }

    public static void showLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }

    /**
     * Function to get the user's current mLocation
     *
     * @return
     */
    private void setTracker() {
    }

    private void updateLocation(Location location) {
        if (location == null)
            return;

        if (isBetterLocation(location, mLocation)) {
            mLocation = location;
            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();
        }
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public Location getLocation() {
        return this.mLocation;
    }

    /**
     * Start using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void startUsingGPS() {
        try {
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, mNetworkListener);

                    updateLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, mGPSListener);

                    updateLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(mNetworkListener);
            locationManager.removeUpdates(mGPSListener);
        }
    }

    /**
     * Function to get mLatitude
     */
    public double getLatitude() {
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
        }

        // return mLatitude
        return mLatitude;
    }

    /**
     * Function to get mLongitude
     */
    public double getLongitude() {
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
        }

        // return mLongitude
        return mLongitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

}
