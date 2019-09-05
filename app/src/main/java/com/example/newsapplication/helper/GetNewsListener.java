package com.example.newsapplication.helper;


import com.example.newsapplication.dummy.NewsItem;

import java.util.ArrayList;
import java.util.List;

public interface GetNewsListener {
    void onGetNewsSuccessful(List<NewsItem> newsList, boolean loadmore);

    void onGetNewsFailed(int failed_id);

}
