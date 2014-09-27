package com.bttendance.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TheFinestArtist on 2013. 12. 7..
 */

public class BTDatabase {

    private static BTSQLiteHelper mSQLiteHelper = null;
    private static Object mSingletonLock = new Object();

    private static BTSQLiteHelper getInstance(Context context) {
        synchronized (mSingletonLock) {
            if (mSQLiteHelper != null)
                return mSQLiteHelper;

            if (context != null) {
                mSQLiteHelper = new BTSQLiteHelper(context);
            }
            return mSQLiteHelper;
        }
    }

    public static void setUUID(Context context, String uuid) {
        getInstance(context).setUUID(uuid);
    }

    public static String getUUID(Context context) {
        return getInstance(context).getUUID();
    }

    public static void setUsername(Context context, String username) {
        getInstance(context).updateUsername(username);
    }

    public static String getUsername(Context context) {
        return getInstance(context).getUsername();
    }

    public static class BTSQLiteHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "BTDB";
        private static final String KEY_UUID = "uuid";
        private static final String KEY_USERNAME = "username";
        private static final String TABLE_NAME_UUID = "UUID";
        private static final String TABLE_CREATE_UUID = "CREATE TABLE " + TABLE_NAME_UUID + " (" + KEY_UUID + " TEXT);";
        private static final String TABLE_NAME_USERNAME = "Username";
        private static final String TABLE_CREATE_USERNAME = "CREATE TABLE " + TABLE_NAME_USERNAME + " (" + KEY_USERNAME + " TEXT);";

        BTSQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE_UUID);
            db.execSQL(TABLE_CREATE_USERNAME);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        // Get All Books
        public String getUUID() {
            String query = "SELECT  * FROM " + TABLE_NAME_UUID;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor;
            if (db != null) {
                cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
                        return cursor.getString(0);
                    } while (cursor.moveToNext());
                }
            }
            return null;
        }

        public void setUUID(String uuid) {
            if (getUUID() != null)
                return;

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_UUID, uuid);
            if (db != null) {
                db.insert(TABLE_NAME_UUID, null, values);
                db.close();
            }
        }

        public void updateUsername(String username) {
            SQLiteDatabase db = this.getWritableDatabase();
            if (db != null) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERNAME);
            }
            setUsername(username);
        }

        // Get All Books
        public String getUsername() {
            String query = "SELECT  * FROM " + TABLE_NAME_USERNAME;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor;
            if (db != null) {
                cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
                        return cursor.getString(1);
                    } while (cursor.moveToNext());
                }
            }
            return null;
        }

        public void setUsername(String username) {
            if (getUsername() != null)
                return;

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, username);
            if (db != null) {
                db.insert(TABLE_NAME_USERNAME, null, values);
                db.close();
            }
        }
    }
}
