package com.example.newsapplication.dummy;

import android.icu.util.LocaleData;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class UrlHelper {

    private String last_update;
    private String last_loadmore;
    private int size;

    public UrlHelper()
    {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

        this.last_update = ft.format(new Date());
        this.last_loadmore = null;
        size = 20;
    }

    public String getURL(String startDate, String endDate)
    {
        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&startDate="+startDate+"&endDate="+endDate+"&words=&categories=";
        return url;
    }

    public String getURL(String startDate, String endDate, String key)
    {
        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&startDate="+startDate+"&endDate="+endDate+"&words="+key+"&categories=";
        return url;
    }





}
