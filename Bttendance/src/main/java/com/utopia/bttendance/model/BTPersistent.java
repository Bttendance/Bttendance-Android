package com.utopia.bttendance.model;

import android.content.Context;
import android.os.Environment;

import com.utopia.bttendance.BTDebug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by TheFinestArtist on 2013. 11. 20..
 */
public class BTPersistent {

    private static final String FILE_NAME = "uuid.data";

    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;
        else
            return false;
    }

    public static String getUUID(Context context) {
        return readFile(context);
    }

    public static void setUUID(Context context, String uuid) {
        writeFile(context, uuid);
    }

    private static void writeFile(Context context, String message) {

        if (!isAvailable()) return;

        File file = new File(context.getExternalFilesDir(null), FILE_NAME);
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(message.getBytes());
            os.close();
        } catch (Exception e) {
            BTDebug.LogError(e.getMessage());
        }
    }

    private static String readFile(Context context) {

        if (!isAvailable()) return null;

        File file = new File(context.getExternalFilesDir(null), FILE_NAME);

        try {
            BufferedReader inputReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputString = inputReader.readLine()) != null) {
                stringBuffer.append(inputString + "\n");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            BTDebug.LogError(e.getMessage());
        }


        return null;
    }

    private static void deleteFile(Context context) {
        File file = new File(context.getExternalFilesDir(null), FILE_NAME);
        if (file != null) {
            file.delete();
        }
    }

    private static boolean hasFile(Context context) {
        File file = new File(context.getExternalFilesDir(null), FILE_NAME);
        if (file != null) {
            return file.exists();
        }
        return false;
    }
}


