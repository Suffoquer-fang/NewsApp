package com.example.newsapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.newsapplication.dummy.JsonHelper;
import com.example.newsapplication.dummy.NewsItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.example.newsapplication.dummy.UrlHelper;

public class GetNewsHelper {
    private GetNewsListener newsListener = null;
    private GetNewsListener searchResultListener = null;
    private GetNewsListener historyListener = null;
    private GetNewsListener favoriteListener = null;

    private static GetNewsHelper instance = null;
    private JsonHelper jsonHelper;
    private OkHttpClient networkClient;

    public MyDBOpenHelper getDBhelper() {
        return DBhelper;
    }

    //    private Context context;
    private MyDBOpenHelper DBhelper;

    //private UrlHelper urlHelper;


    private static int NORMAL = 0;
    private static int HISTORY = 1;
    private static int FAVORITE = 2;


    public GetNewsHelper(Context c) {
        jsonHelper = new JsonHelper();
        networkClient = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.SECONDS).readTimeout(1, TimeUnit.SECONDS).retryOnConnectionFailure(false).build();
        DBhelper = new MyDBOpenHelper(c, "TEST1.db", null, 1);
//        db = DBhelper.getReadableDatabase();
    }

    public GetNewsHelper getInstance(Context c) {
        return new GetNewsHelper(c);

    }

    public void setNewsListener(GetNewsListener newsListener) {
        this.newsListener = newsListener;
    }

    public void setSearchResultListener(GetNewsListener searchResultListener) {
        this.searchResultListener = searchResultListener;
    }

    public void setHistoryListener(GetNewsListener historyListener) {
        this.historyListener = historyListener;
    }

    public void setFavoriteListener(GetNewsListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }


    private void getNewsFromNetwork(int size, String channel, String startDate, String endDate, String keyword, Callback callback) {
        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size=30&startDate=2019-07-01&endDate=2019-07-03&words="+ keyword+ "&categories=" + channel;
        final Request request = new Request.Builder().url(url).get().build();
        Call call = networkClient.newCall(request);
        call.enqueue(callback);
    }

    public List<NewsItem> getNewsFromDB(int size, String channel, String startDate, String endDate, String keyword, int type) {
//        DBhelper = new MyDBOpenHelper(context, "TEST1.db", null, 1);

        //return new ArrayList<>();
        startDate = "2000-01-01";
        endDate = "2019-8-30";

        Cursor c = null;

        if (type == NORMAL)                //normal
        {
            System.out.println("query normal");
            c = DBhelper.getReadableDatabase().query("News", new String[]{"_id", "title", "author", "pubtime", "content", "NewsId", "isHIs"}, "channel = ?", new String[]{channel}, null, null, "pubtime DESC");

        } else if (type == HISTORY)            //history
        {
            System.out.println("q history");
            c = DBhelper.getReadableDatabase().query("News", new String[]{"_id", "title", "author", "pubtime", "content", "NewsId", "isHIs"}, "isHis = 1", null, null, null, "pubtime DESC");

        } else if(type == FAVORITE)                       //Fav
        {
            c = DBhelper.getReadableDatabase().query("News", new String[]{"_id", "title", "author", "pubtime", "content", "NewsId", "isHIs"}, "isFav = 1", null, null, null, "pubtime DESC");
        }
        System.out.println("get from db");

        List<NewsItem> retList = new ArrayList<>();
        if(c == null) return retList;
        System.out.println("c != null");
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String title = c.getString(c.getColumnIndex("title"));
                String author = c.getString(c.getColumnIndex("author"));
                String pubtime = c.getString(c.getColumnIndex("pubtime"));
                String content = c.getString(c.getColumnIndex("content"));
                String NewsId = c.getString(c.getColumnIndex("NewsId"));
                int His = c.getInt(c.getColumnIndex("isHis"));
                NewsItem i = new NewsItem(title, author, pubtime, null, content, NewsId);
                if(His == 1)
                    i.setInHistory(true);

                retList.add(i);
                c.moveToNext();
            }
        }
        c.close();
        DBhelper.close();
        return retList;
    }

    private void getNews(final int size, final String channel, final String startDate, final String endDate, final String keyword, final GetNewsListener listener, final boolean loadmore) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.out.println("Net failed");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        SQLiteDatabase db = DBhelper.getReadableDatabase();
//                        List<NewsItem> ret = new ArrayList<>();
                        List<NewsItem> ret = getNewsFromDB(size, channel, startDate, endDate, keyword, NORMAL);
                        if(ret.size() > 0)
                            listener.onGetNewsSuccessful(ret, loadmore);
                        else
                            listener.onGetNewsFailed(0);
                    }
                }).start();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                jsonHelper.parse(json);
                List<NewsItem> list = jsonHelper.list;


                checkForHis(list);


                listener.onGetNewsSuccessful(list, loadmore);
            }
        };

        getNewsFromNetwork(size, channel, startDate, endDate, keyword, callback);
    }

    public void checkForHis(List<NewsItem> list)
    {
        for (NewsItem item : list) {
            if (isInHistory(item.getmNewsID()))
                item.setInHistory(true);
            else
            {
                addNormal(item);
            }
        }
    }


    public void requestUpdateNews(int size, String channel) {
        if(channel.equals("推荐"))
            getRecommendNews(size, false);
        else
            getNews(size, channel, "", "", "", newsListener, false);

    }

    public void requestLoadMoreNews(int size, String channel) {
        if(channel.equals("推荐"))
            getRecommendNews(size, true);
        else
            getNews(size, channel, "", "", "", newsListener, true);
    }

    public void getRecommendNews(int size, boolean loadmore)
    {
        getNews(size, "", "", "", "", newsListener, loadmore);
    }

    public void requestSearchResult(int size, final String keyword) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                searchResultListener.onGetNewsFailed(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                jsonHelper.parse(json);
                List<NewsItem> list = jsonHelper.list;

                List<NewsItem> retList = new ArrayList<>();

                for(NewsItem item : list)
                {
                    if(item.getmTitle().contains(keyword))
                        retList.add(0, item);
                    else
                        retList.add(item);
                }



                searchResultListener.onGetNewsSuccessful(retList, false);
            }
        };
        getNewsFromNetwork(size, "", "", "", keyword, callback);
    }

    public void requestHistory(final int size, final boolean loadmore) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsItem> ret = getNewsFromDB(size, "", "", "", "", HISTORY);
                historyListener.onGetNewsSuccessful(ret, loadmore);
            }
        }).start();

    }

    public void requestFavorite(final int size, final boolean loadmore) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsItem> ret = getNewsFromDB(size, "", "", "", "", FAVORITE);
                favoriteListener.onGetNewsSuccessful(ret, loadmore);
            }
        }).start();
    }


    public boolean deleteHistory(NewsItem del) {
        del.setInHistory(false);
        ContentValues cv = new ContentValues();
        cv.put("isHis", 0);
        DBhelper.getReadableDatabase().update("News", cv, "NewsId = ?", new String[]{del.getmNewsID()});
     //   db.close();
        DBhelper.close();
        return true;
    }

    public boolean deleteFavorite(NewsItem del) {
        ContentValues cv = new ContentValues();
        cv.put("isFav", 0);
        DBhelper.getReadableDatabase().update("News", cv, "NewsId = ?", new String[]{del.getmNewsID()});

        DBhelper.close();
        return true;
    }

    public boolean addNormal(NewsItem item) {

        Cursor c = DBhelper.getReadableDatabase().query("News", new String[]{"_id", "NewsId"}, "NewsId = ?", new String[]{item.getmNewsID()}, null, null, null);
        if(c.moveToFirst()) return false;
        System.out.println("add normal");
        ContentValues cv = new ContentValues();
        cv.put("title", item.getmTitle());
        cv.put("author", item.getmAuthor());
        cv.put("pubtime", item.getmPubTime());
        cv.put("content", item.getmContent());
        cv.put("channel", item.getmChannel());
        cv.put("NewsId", item.getmNewsID());
        cv.put("isHis", 0);
        cv.put("isFav", 0);
        DBhelper.getReadableDatabase().insert("News", null, cv);
        DBhelper.close();

//        db.close();
        return true;
    }

    public boolean addHistory(NewsItem item) {
        item.setInHistory(true);
        ContentValues cv = new ContentValues();
        cv.put("isHis", 1);
        int ret = DBhelper.getReadableDatabase().update("News", cv, "NewsId = ?", new String[]{item.getmNewsID()});
        //db.close();
        DBhelper.close();
        return true;
    }

    public boolean addFavorite(NewsItem item) {
        ContentValues cv = new ContentValues();
        cv.put("isFav", 1);
        int ret = DBhelper.getReadableDatabase().update("News", cv, "NewsId = ?", new String[]{item.getmNewsID()});
        //db.close();
        DBhelper.close();
        return true;
    }

    public boolean isInHistory(String newsID) {
        //return false;
        if(newsID == null) return false;

        Cursor c = DBhelper.getReadableDatabase().query("News", new String[]{"_id", "isHis"}, "NewsId = ?", new String[]{newsID}, null, null, null);
        if(!c.moveToFirst()) return false;
        if (c.getInt(c.getColumnIndex("isHis")) == 1){
            c.close();
            DBhelper.close();
            return true;
        }
        else {
            c.close();
            DBhelper.close();
            return false;
        }
    }

    public boolean isInFavorite(String newsID) {
        Cursor c = DBhelper.getReadableDatabase().query("News", new String[]{"_id", "isFav"}, "NewsId = ?", new String[]{newsID}, null, null, null);
        //db.close();
        if(!c.moveToFirst()) return false;
        if (c.getInt(c.getColumnIndex("isFav")) == 1) {
            c.close();
            DBhelper.close();
            return true;
        }
        else {
            c.close();
            DBhelper.close();
            return false;
        }
    }


}

