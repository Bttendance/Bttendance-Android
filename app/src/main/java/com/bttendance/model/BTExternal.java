package com.bttendance.model;

import android.content.Context;
import android.os.Environment;

import com.bttendance.BTDebug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTExternal {

    private static final String FILE_NAME = "bttendance_ringtone.m4a";

    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;
        else
            return false;
    }

    public static String getUUID() {
        return readFile();
    }

    public static void setUUID(String uuid) {
        writeFile(uuid);
    }

    private static void writeFile(String message) {
        if (!isAvailable()) return;

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
        File file = new File(path, FILE_NAME);

        try {
            path.mkdir();

//            if (path.canWrite())
//            {
//                BTDebug.LogError("a");
//                FileWriter filewriter = new FileWriter(file,true);
//                BTDebug.LogError("b");
//                BufferedWriter out = new BufferedWriter(filewriter);
//                BTDebug.LogError("c");
//                out.write(message);
//                BTDebug.LogError("d");
//                out.close();
//            }
            BTDebug.LogError("writable : " + path.canWrite());
            BTDebug.LogError("a");
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            BTDebug.LogError("b");
            os.write(message.getBytes());
            BTDebug.LogError("c");
            os.close();
            BTDebug.LogError("d");
        } catch (Exception e) {
            BTDebug.LogError("e");
            e.printStackTrace();
            BTDebug.LogError(e.getMessage());
        }
    }

    private static String readFile() {
        if (!isAvailable()) return null;

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS), FILE_NAME);

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString);
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            BTDebug.LogError(e.getMessage());
        }

        return null;
    }

    private static boolean deleteFile(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS), FILE_NAME);
        return file.delete();
    }

    private static boolean hasFile(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS), FILE_NAME);
        return file.exists();
    }
}