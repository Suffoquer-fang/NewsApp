package com.example.newsapplication.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {
    private static SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTime()
    {
        return ft.format(new Date());
    }

    public static String timeBefore(String now, int t)
    {
        Date date = null;
        if(now == null || now.equals(""))
            return ft.format(new Date());
        try {
            date = ft.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setTime(date.getTime() - t * 1000);

        return ft.format(date);
    }

    public static String timeAfter(String now, int t)
    {
        Date date = null;
        if(now == null || now.equals(""))
            return ft.format(new Date());
        try {
            date = ft.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setTime(date.getTime() + t * 1000);
        return ft.format(date);
    }



}
