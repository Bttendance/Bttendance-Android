package com.bttendance.agent;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by TheFinestArtist on 2013. 11. 1..
 */
public class BluetoothHelper {


    // Found UUID list
    private static Map<String, String> UUIDLIST = new ConcurrentHashMap<String, String>();
    private static Map<String, String> UUIDLISTSENDED = new ConcurrentHashMap<String, String>();
//    public static int ATTENDANCE_STARTING_COURSE = -1;

    public static synchronized void UUIDLIST_add(String mac) {
        UUIDLIST.put(mac, mac);
    }

    public static synchronized Map<String, String> UUIDLIST() {
        return UUIDLIST;
    }

    public static synchronized void UUIDLIST_refresh() {
        UUIDLIST = new ConcurrentHashMap<String, String>();
    }

    public static synchronized Map<String, String> UUIDLISTSENDED() {
        return UUIDLISTSENDED;
    }

    public static synchronized void UUIDLISTSENDED_addAll(Map<String, String> map) {
        UUIDLISTSENDED.putAll(map);
    }

    public static synchronized void UUIDLISTSENDED_refresh() {
        UUIDLISTSENDED = new ConcurrentHashMap<String, String>();
    }

    public static synchronized boolean UUIDLISTSENDED_contains(String mac) {
        return UUIDLISTSENDED.containsKey(mac);
    }

    public static synchronized void UUIDLISTSENDED_remove(String mac) {
        UUIDLISTSENDED.remove(mac);
    }

//    public static synchronized long getRefreshTimeTo() {
//
//        long currentTime = DateHelper.getCurrentGMTTimeMillis();
//        long timeTo = currentTime;
//
//        for (int i = 0; i < BTTable.PostTable.size(); i++) {
//            int key = BTTable.PostTable.keyAt(i);
//            PostJson post = BTTable.PostTable.get(key);
//            if (post.createdAt != null
//                    && "attendance".equals(post.type)
//                    && AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
//                    && currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION) {
//                if (timeTo > currentTime)
//                    timeTo = Math.min(timeTo, DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION);
//                else
//                    timeTo = DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION;
//            }
//
//            if (post.createdAt != null
//                    && "clicker".equals(post.type)
//                    && currentTime - DateHelper.getTime(post.createdAt) < (post.clicker.progress_time + 5) * 1000) {
//                if (timeTo > currentTime)
//                    timeTo = Math.min(timeTo, DateHelper.getTime(post.createdAt) + (post.clicker.progress_time + 5) * 1000);
//                else
//                    timeTo = DateHelper.getTime(post.createdAt) + (post.clicker.progress_time + 5) * 1000;
//            }
//        }
//
//        if (timeTo > currentTime)
//            return timeTo;
//
//        return -1;
//    }
//
//    public static synchronized long getAttdChekTimeTo() {
//        long currentTime = DateHelper.getCurrentGMTTimeMillis();
//        long timeTo = currentTime;
//
//        for (int i = 0; i < BTTable.PostTable.size(); i++) {
//            int key = BTTable.PostTable.keyAt(i);
//            PostJson post = BTTable.PostTable.get(key);
//            if (post.createdAt != null
//                    && "attendance".equals(post.type)
//                    && AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
//                    && currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION
//                    && timeTo < DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION) {
//                timeTo = DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION;
//            }
//        }
//
//        return timeTo;
//    }
//
//    public static synchronized Set<Integer> getAttdCheckingIds() {
//        Set<Integer> checking = new HashSet<Integer>();
//
//        for (int i = 0; i < BTTable.PostTable.size(); i++) {
//            int key = BTTable.PostTable.keyAt(i);
//            PostJson post = BTTable.PostTable.get(key);
//            if (post.createdAt != null
//                    && "attendance".equals(post.type)
//                    && AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
//                    && DateHelper.getCurrentGMTTimeMillis() - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION) {
//                checking.add(post.attendance.id);
//            }
//        }
//
//        return checking;
//    }

    public static final int REQUEST_ENABLE_BT = 21;
    public static final int REQUEST_ENABLE_DISCOVERABILITY_BT = 22;
    public static final int DISCOVERABILITY_BT_DURATION = 0;
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

    public static boolean isDiscoverable() {
        if (!isAvailable())
            return false;

        return mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }

    public static int getScanMode() {
        if (!isAvailable())
            return -1;

        return mBluetoothAdapter.getScanMode();
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
            if (mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.startDiscovery();
        }
    }

    public static String getMacAddress() {
        if (!isAvailable())
            return null;

        if (mBluetoothAdapter.getAddress() == null)
            enableWithUI();

        return mBluetoothAdapter.getAddress();
    }

}
