package com.example.Fangyan.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Fangyan.dummy.JsonHelper;
import com.example.Fangyan.dummy.NewsItem;
import com.example.Fangyan.dummy.UrlHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private Context context;
    private MyDBOpenHelper DBhelper;

    private UrlHelper urlHelper;


    private static int NORMAL = 0;
    private static int HISTORY = 1;
    private static int FAVORITE = 2;



    public GetNewsHelper(Context c) {
        jsonHelper = new JsonHelper();
        networkClient = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).readTimeout(2, TimeUnit.SECONDS).retryOnConnectionFailure(false).build();
        DBhelper = MyDBOpenHelper.getInstance(c, "TEST1.db", null, 1);
//        db = DBhelper.getReadableDatabase();
        urlHelper = new UrlHelper();
        context = c;
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
        String url = urlHelper.getURL(size, channel, startDate, endDate, keyword);
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
            c = DBhelper.getDatabase().query("News", new String[]{"_id", "title", "author", "pubtime", "content", "NewsId", "isHIs", "keywords"}, "channel = ?", new String[]{channel}, null, null, "pubtime DESC");

        } else if (type == HISTORY)            //history
        {
            System.out.println("q history");
            c = DBhelper.getDatabase().query("News", new String[]{"_id", "title", "author", "pubtime", "content", "NewsId", "isHIs", "keywords"}, "isHis = 1", null, null, null, "pubtime DESC");

        } else if(type == FAVORITE)                       //Fav
        {
            c = DBhelper.getDatabase().query("News", new String[]{"_id", "title", "author", "pubtime", "content", "NewsId", "isHIs", "keywords"}, "isFav = 1", null, null, null, "pubtime DESC");
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

                byte[] arr = c.getBlob(c.getColumnIndex("keywords"));
                ByteArrayInputStream stream = new ByteArrayInputStream(arr);

                List<String> keys = new ArrayList<>();
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(stream);
                    keys =  (List<String>)objectInputStream.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


                NewsItem i = new NewsItem(title, author, pubtime, null, content, NewsId);
                if(His == 1)
                    i.setInHistory(true);
                i.setmKeywords(keys);

                retList.add(i);
                c.moveToNext();
            }
        }
        c.close();
        DBhelper.closeDatabase();
        return retList;
    }

    private void getNews(final int size, final String channel, final String startDate, final String endDate, final String keyword, final GetNewsListener listener, final boolean loadmore) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.out.println("Net failed");
                if(channel.equals("推荐"))
                    listener.onGetNewsFailed(0);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                System.out.println("On response");

                String json = response.body().string();
                System.out.println(json);
                if(json.length() == 0)
                {
                    listener.onGetNewsFailed(1);
                    return;
                }

                jsonHelper.parse(json);
                List<NewsItem> list = new ArrayList<>();
                list.addAll(jsonHelper.list);


                checkForHis(list);


                listener.onGetNewsSuccessful(list, loadmore);
            }
        };

        getNewsFromNetwork(size, channel, startDate, endDate, keyword, callback);
    }

    public void checkForHis(List<NewsItem> list)
    {
        SQLiteDatabase db = DBhelper.getDatabase();
        if(list == null || list.size() == 0)
            return;
        for (NewsItem item : list) {
            Cursor c = db.query("News", new String[]{"_id", "isHis", "isFav"}, "NewsId = ?", new String[]{item.getmNewsID()}, null, null, null);
            if(!c.moveToFirst())
            {
                ContentValues cv = new ContentValues();
                cv.put("title", item.getmTitle());
                cv.put("author", item.getmAuthor());
                cv.put("pubtime", item.getmPubTime());
                cv.put("content", item.getmContent());
                cv.put("channel", item.getmChannel());
                cv.put("NewsId", item.getmNewsID());
                cv.put("isHis", 0);
                cv.put("isFav", 0);

                List<String> tmp = item.getmKeywords();
                if(tmp == null) tmp = new ArrayList<>();
                ByteArrayOutputStream stream =  new ByteArrayOutputStream();
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(stream);
                    outputStream.writeObject(tmp);
                    outputStream.flush();
                    byte[] arr = stream.toByteArray();
                    cv.put("keywords", arr);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                db.insert("News", null, cv);
            }
            else
            {
                int his = c.getInt(c.getColumnIndex("isHis"));
                int fav = c.getInt(c.getColumnIndex("isFav"));
                item.setInHistory(his == 1);
                item.setInFavorite(fav == 1);
            }

        c.close();
        }

        DBhelper.closeDatabase();
    }


    public void requestUpdateNews(int size, String channel, String startDate) {
        if(channel.equals("推荐"))
            getRecommendNews(size, false);
        else
            getNews(size, channel, startDate, "", "", newsListener, false);

    }

    public void requestLoadMoreNews(int size, String channel) {


        if(channel.equals("推荐"))
            getRecommendNews(size, true);
        else {
            getNews(size, channel, "", "", "", newsListener, true);
        }
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
                List<NewsItem> list = new ArrayList<>();
                list.addAll(jsonHelper.list);


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
        DBhelper.getDatabase().update("News", cv, "NewsId = ?", new String[]{del.getmNewsID()});
     //   db.close();
        DBhelper.closeDatabase();
        return true;
    }

    public boolean deleteFavorite(NewsItem del) {
        del.setInFavorite(false);
        ContentValues cv = new ContentValues();
        cv.put("isFav", 0);
        DBhelper.getDatabase().update("News", cv, "NewsId = ?", new String[]{del.getmNewsID()});

        DBhelper.closeDatabase();
        return true;
    }

    public boolean addNormal(NewsItem item) {

        Cursor c = DBhelper.getDatabase().query("News", new String[]{"_id", "NewsId"}, "NewsId = ?", new String[]{item.getmNewsID()}, null, null, null);
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

        List<String> tmp = item.getmKeywords();
        ByteArrayOutputStream stream =  new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(stream);
            outputStream.writeObject(tmp);
            outputStream.flush();
            byte[] arr = stream.toByteArray();
            cv.put("keywords", arr);

        } catch (IOException e) {
            e.printStackTrace();
        }


        DBhelper.getDatabase().insert("News", null, cv);
        DBhelper.closeDatabase();

//        db.close();
        return true;
    }

    public boolean addHistory(NewsItem item) {
        item.setInHistory(true);
        ContentValues cv = new ContentValues();
        cv.put("isHis", 1);
        int ret = DBhelper.getDatabase().update("News", cv, "NewsId = ?", new String[]{item.getmNewsID()});
        //db.close();
        DBhelper.closeDatabase();
        return true;
    }

    public boolean addFavorite(NewsItem item) {
        ContentValues cv = new ContentValues();
        cv.put("isFav", 1);
        int ret = DBhelper.getDatabase().update("News", cv, "NewsId = ?", new String[]{item.getmNewsID()});
        //db.close();
        DBhelper.closeDatabase();
        return true;
    }

    public boolean isInHistory(String newsID) {
        //return false;
        if(newsID == null) return false;

        Cursor c = DBhelper.getDatabase().query("News", new String[]{"_id", "isHis"}, "NewsId = ?", new String[]{newsID}, null, null, null);
        if(!c.moveToFirst()) {
            c.close();
            DBhelper.closeDatabase();
            return false;
        }
        if (c.getInt(c.getColumnIndex("isHis")) == 1){
            c.close();
            DBhelper.closeDatabase();
            return true;
        }
        else {
            c.close();
            DBhelper.closeDatabase();
            return false;
        }
    }

    public boolean isInFavorite(String newsID) {
        Cursor c = DBhelper.getDatabase().query("News", new String[]{"_id", "isFav"}, "NewsId = ?", new String[]{newsID}, null, null, null);
        //db.close();

        if(!c.moveToFirst()) {
            c.close();
            DBhelper.closeDatabase();
            return false;
        }
        if (c.getInt(c.getColumnIndex("isFav")) == 1) {
            c.close();
            DBhelper.closeDatabase();
            return true;
        }
        else {
            c.close();
            DBhelper.closeDatabase();
            return false;
        }
    }

    public boolean isInNormal(String newsID)
    {
        Cursor c = DBhelper.getDatabase().query("News", new String[]{"_id", "NewsId"}, "NewsId = ?", new String[]{newsID}, null, null, null);
        //db.close();
        if(!c.moveToFirst()) return false;
        return true;
    }



    public void getNewsFromPureNetwork(String channel, String start, String end, final String key, final GetNewsListener listener, final boolean loadmore)
    {

        if(channel.equals("推荐"))
        {
            getRecommendNews("", end, listener, loadmore);
            return;
        }


        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onGetNewsFailed(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();
                System.out.println(json);

                jsonHelper.parse(json);
                List<NewsItem> list = new ArrayList<>();
                list.addAll(jsonHelper.list);

                checkForHis(list);
                if(!key.equals(""))
                {
                    List<NewsItem> retList = new ArrayList<>();



                    for (NewsItem item : list) {
                       if(isBad(item.getmKeywords())) continue;
                        if (item.getmTitle().contains(key))
                            retList.add(0, item);
                        else
                            retList.add(item);
                    }
                    listener.onGetNewsSuccessful(retList, loadmore);
                    return;
                }



                listener.onGetNewsSuccessful(list, loadmore);
            }
        };

        getNewsFromNetwork(20, channel, start, end, key, callback);
    }



    public void getNewsOffline(final int size, final String channel, final GetNewsListener listener)
    {

        if(channel.equals("推荐"))
        {
            listener.onGetNewsFailed(2);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsItem> ret = getNewsFromDB(size, channel, "", "", "", NORMAL);
                if(ret.size() > 0)
                    listener.onGetNewsSuccessful(ret, false);
                else
                    listener.onGetNewsFailed(2);
            }
        }).start();
    }


    public void getRecommendNews(final String start, final String end, final GetNewsListener listener, final boolean loadmore)
    {
        final List<NewsItem> items = getNewsFromDB(10, "", "", "", "", FAVORITE);
        if(items.size() < 2)
            items.addAll(getNewsFromDB(10, "", "", "", "", HISTORY));

        if(items.size() < 2)
        {
            listener.onGetNewsFailed(2);
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsItem> retList = new ArrayList<>();
                List<NewsItem> ret = new ArrayList<>();
                int size = 10;
                while(ret.size() <= 25 && size < 50) {
                    int r= new Random().nextInt(items.size());
                    final String key1 = items.get(r).getmKeywords().get(0);
                    retList.addAll(getSearchResult(size + 10, start, end, key1));
                    size += 10;
                    for (NewsItem item : retList) {
                        if (!ret.contains(item) && !isInHistory(item.getmNewsID())) {
                           if(isBad(item.getmKeywords())) continue;
                            ret.add(0, item);
                            addNormal(item);
                        }
                    }
                }

                listener.onGetNewsSuccessful(ret, loadmore);
            }
        }).start();

    }

    List<NewsItem> getSearchResult(int size, String start, String end, String key)
    {
        String url = urlHelper.getURL(10, "", start, end, key);
        final Request request = new Request.Builder().url(url).get().build();
        Call call = networkClient.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful())
            {
                String json = response.body().string();
                System.out.println(json);
                jsonHelper.parse(json);
                List<NewsItem> list = new ArrayList<>();
                list.addAll(jsonHelper.list);

                checkForHis(list);



                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    public void addNewKeyword(String add)
    {
        SQLiteDatabase db = DBhelper.getDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Content", add);
        db.insert("BadKeys", null, cv);
        DBhelper.closeDatabase();
    }

    public boolean isBad(List<String> keys)
    {
        SQLiteDatabase db = DBhelper.getDatabase();
        boolean flag = false;
        for(String key : keys) {
            Cursor c = db.query("BadKeys", new String[]{"_id", "Content"}, "Content = ?", new String[]{key}, null, null, null);
            if (c.moveToFirst()) {
                flag = true;
                c.close();
                break;
                }
            c.close();
        }
        DBhelper.closeDatabase();
        return flag;
    }

    public synchronized void clear()
    {
        SQLiteDatabase db = DBhelper.getDatabase();
        db.delete("BadKeys", null, null);
        DBhelper.closeDatabase();
    }


}

