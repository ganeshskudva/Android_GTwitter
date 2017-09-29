package com.example.gkudva.android_gtwitter.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by gkudva on 28/09/17.
 */

public class DateUtil {
    private static final String TAG = DateUtil.class.getSimpleName();
    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";


    private static long getDateMillis(String rawJsonDate) throws ParseException {
        long dateMillis = 0;

        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);
        dateMillis = sf.parse(rawJsonDate).getTime();
        return dateMillis;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String relativeDate = "";
        try {
            long dateMillis = getDateMillis(rawJsonDate);
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            Log.d(TAG, "Exception from getRelativeTimeAgo()");
        }

        Log.d(TAG, "getRelativeTimeAgo: " + relativeDate);
        return relativeDate;
    }

    public static String getFormattedDate(Context context, String rawJsonDate) {
        String date = "";
        try {
            long dateMillis = getDateMillis(rawJsonDate);
            date = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH);
        } catch (ParseException e) {
            Log.d(TAG, "Exception from getFormattedDate()");
        }

        Log.d(TAG, "getFormattedDate: " + date);
        return date;
    }

    public static String getFormattedTime(Context context, String rawJsonDate) {
        String time = "";
        try {
            long dateMillis = getDateMillis(rawJsonDate);
            time = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_TIME);
        } catch (ParseException e) {
            Log.d(TAG, "Exception from getFormattedTime()");
        }

        Log.d(TAG, "getFormattedTime: " + time);
        return time;
    }
}
