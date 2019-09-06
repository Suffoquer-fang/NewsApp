package com.example.newsapplication.dummy;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UrlHelper {

    private String last_update;
    private String last_loadmore;
    private SimpleDateFormat ft;
    private int size;

    public UrlHelper()
    {
        ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

        this.last_update = ft.format(new Date());
        this.last_loadmore = null;
        size = 20;
    }

    public String getURL(String startDate, String endDate)
    {
        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&startDate="+startDate+"&endDate="+endDate+"&words=&categories=";
        return url;
    }

    public String getURL(int size, String channel, String startDate, String endDate, String key)
    {

        if(endDate.equals(""))
        {
            endDate = ft.format(new Date());
        }

        System.out.println("start" + startDate);

        System.out.println("end" + endDate);

        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&startDate="+startDate+"&endDate="+endDate+"&words="+key+"&categories="+channel;
        return url;
    }



    public String getLoadMoreUrl(int size, String channel, String key)
    {
        Date date = new Date();
        date.setTime(date.getTime() - 24 * 60 * 60 * 1000);
        String endDate = ft.format(new Date());
        String startDate = ft.format(date);
        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&startDate="+startDate+"&endDate="+endDate+"&words="+key+"&categories="+channel;
        return url;
    }







}
