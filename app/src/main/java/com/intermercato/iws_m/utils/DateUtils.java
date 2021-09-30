package com.intermercato.iws_m.utils;



import com.intermercato.iws_m.mApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import se.oioi.intelweighblelib.helpers.Language;



public class DateUtils {

    public static String getPreFormattedDate(long date){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Language.getCurrentLocale(mApplication.Companion.applicationContext()));
        return df.format(cal.getTime());
    }

    public static String getPreFormattedDateDetailed(long date){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date*1000);

        DateFormat df = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.MEDIUM,
                Language.getCurrentLocale(mApplication.Companion.applicationContext()));

        return df.format(cal.getTime());
    }

    public static String getFormattedDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Date date = new Date(milliSeconds);
        return df.format(date);
    }

    public static String formatLongToStringTime(long time){

        Date d = new Date(time);
        DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        return df.format(d);
    }

    public static String formatLongToStringTime(long time, String strformat){
        Date d = new Date(time);
        TimeZone tzGMT = TimeZone.getDefault();
        DateFormat format = new SimpleDateFormat(strformat);
        format.setTimeZone(tzGMT);
        return format.format(d);
    }

    public static String formatISO8601(long time, String strformat){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(strformat); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);

      return df.format(new Date(time));

    }

}
