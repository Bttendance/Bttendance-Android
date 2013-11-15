package com.utopia.bttendance.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;


import android.os.Build;
import android.os.Handler;
import android.view.*;
import android.widget.BaseAdapter;
import java.util.ArrayList;


/**
 * Created by TheFinestArtist on 2013. 11. 1..
 */
public class BluetoothHelper {

    public static final int REQUEST_ENABLE_BT = 21;
    public static final int REQUEST_ENABLE_DISCOVERABILITY_BT = 2200;
    public static final int DISCOVERABILITY_BT_DURATION = 300;

    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //public static boolean mScanning;
    //public static final long SCAN_PERIOD = 10000; //mssec
    //public static Handler mHandler;
    //public static LeDeviceListAdapter mLeDeviceListAdapter;

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
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_BT_DURATION);
        activity.startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABILITY_BT);
    }

    public static void startDiscovery() {
        if (!isAvailable())
            return;

        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    public static void startBroadcast(){

    }

//    public static void startLescan(BluetoothAdapter.LeScanCallback callback) {
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    public static void startLescan(final boolean enable){
////        if (!isAvailable())
////            return;
//
////        if (mBluetoothAdapter.isEnabled()) {
////            mBluetoothAdapter.startLeScan(callback);
//
//        if(enable){
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }
//            }, SCAN_PERIOD);
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        }else{
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//    }
//
//    private class LeDeviceListAdapter {
//        private ArrayList<BluetoothDevice> mLeDevices;
//        private LayoutInflater mInflator;
//
//        public LeDeviceListAdapter() {
//            super();
//            mLeDevices = new ArrayList<BluetoothDevice>();
//            //mInflator = DeviceScanActivity.this.getLayoutInflater();
//        }
//
//        public void addDevice(BluetoothDevice device) {
//            if(!mLeDevices.contains(device)) {
//                mLeDevices.add(device);
//            }
//        }
//
//        public BluetoothDevice getDevice(int position) {
//            return mLeDevices.get(position);
//        }
//
//        public void clear() {
//            mLeDevices.clear();
//        }
//
//        //@Override
//        public int getCount() {
//            return mLeDevices.size();
//        }
//
//        //@Override
//        public Object getItem(int i) {
//            return mLeDevices.get(i);
//        }
//
//        //@Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//    }
//
//
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private static BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mLeDeviceListAdapter.addDevice(device);
//                            //mLeDeviceListAdapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//            };



}
