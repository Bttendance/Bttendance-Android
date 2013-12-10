package com.utopia.bttendance.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by TheFinestArtist on 2013. 11. 1..
 */
public class BluetoothHelper {

    public static final int REQUEST_ENABLE_BT = 21;
    public static final int REQUEST_ENABLE_DISCOVERABILITY_BT = 22;
    public static final int DISCOVERABILITY_BT_DURATION = 600;
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public static boolean isAvailable() {
        if (mBluetoothAdapter == null)
            return false;
        return true;
    }

    public static void enable(Activity activity) {
        if (!isAvailable())
            return;

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public static void enableWithUI() {
        if (!isAvailable())
            return;

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    public static void disableWithUI() {
        if (!isAvailable())
            return;

        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public static void enableDiscoverability(Activity activity) {
        mBluetoothAdapter.setName(UUIDHelper.getUUID(activity));
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_BT_DURATION);
        activity.startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABILITY_BT);
    }

    public static void startDiscovery() {
        if (!isAvailable())
            return;

        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.startDiscovery();
        }
    }

    public static String getMacAddress() {
        if (!isAvailable())
            return null;

        return mBluetoothAdapter.getAddress();
    }

}
