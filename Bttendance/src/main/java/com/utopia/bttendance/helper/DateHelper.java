
package com.utopia.bttendance.helper;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.z");

    private DateHelper() {
    }

    public static String getTimeAgoString(Context context, String timeStr) {

        if (timeStr == null || timeStr.length() == 0)
            return "";

        Date date;
        try {
            date = date_format.parse(timeStr);
            CharSequence time_ago_str = DateUtils.getRelativeDateTimeString(
                    context, date.getTime(), DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS, 0);
            String string = time_ago_str.toString();
            int index = string.lastIndexOf(",");
            if (index > 0)
                string = string.substring(0, index);
            return string;
        } catch (ParseException e) {
            return "";
        }
    }

    public static CharSequence getTimeAgoString(Context context, long currentTimeMillis) {
        CharSequence time_ago_str = DateUtils.getRelativeDateTimeString(
                context, currentTimeMillis, DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, 0);
        if (time_ago_str == null)
            return "";

        String string = time_ago_str.toString();
        int index = string.lastIndexOf(",");
        if (index > 0)
            string = string.substring(0, index);
        return string;
    }

    public static String getCurrentTimeString() {
        return (String) DateFormat.format("yyyy-MM-dd'T'HH:mm:ss.z", System.currentTimeMillis());
    }

    public static String getString(Date date) {
        return date_format.format(date);
    }

    public static long getTime(String timeStr) {
        Date date;
        try {
            date = date_format.parse(timeStr);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static Date getDate(String timeStr) {
        Date date;
        try {
            date = date_format.parse(timeStr);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String geFomrattedNumberString(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String getFormattedCollectionTitle(String title) {
        return title.replaceAll("[\n\t ]+", " ").trim();
    }

}// end of class
