
package com.utopia.bttendance.helper;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatHelper {

    private static SimpleDateFormat date_format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss Z");

    private FormatHelper() {
    }

    public static String getTimeAgoString(Context context, String time_str) {

        if (time_str == null || time_str.length() == 0)
            return "";

        Date date;
        try {
            date = date_format.parse(time_str);
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
        return (String) DateFormat.format("yyyy-MM-dd hh:mm:ss z", System.currentTimeMillis());
    }

    public static long getTime(String time_str) {
        Date date;
        try {
            date = date_format.parse(time_str);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static Date getDate(String time_str) {
        Date date;
        try {
            date = date_format.parse(time_str);
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
