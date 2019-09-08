package com.example.Fangyan.helper;


import com.example.Fangyan.dummy.NewsItem;

import java.util.List;

public interface GetNewsListener {
    void onGetNewsSuccessful(List<NewsItem> newsList, boolean loadmore);

    void onGetNewsFailed(int failed_id);

}
