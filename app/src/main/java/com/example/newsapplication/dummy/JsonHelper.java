package com.example.newsapplication.dummy;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class JsonHelper {
    public List<NewsItem> list;

    public void parse(String jex) {
        list = new LinkedList<>();
        Gson g = new Gson();
        JsonType json = g.fromJson(jex, JsonType.class);
        List<JsonNews> news = json.data;
        for (JsonNews i : news) {
            String tmp = i.image;
            List<String> img  = new LinkedList<>();
            String[] arr = tmp.replaceAll("\\[|]", "").split(", ");
            for(String j: arr){
                img.add(j);
            }

            if(img.size() == 1 && img.get(0).length() == 0)
                img = null;
            NewsItem item = new NewsItem(i.title, i.publisher, i.publishTime, img, i.content, i.newsID);
            item.setmChannel(i.category);

            List<String> key = new ArrayList<>();

            for(JsonKeyWordOrWhenOrWhereOrWho w : i.keywords)
            {
                key.add(w.word);
            }
            item.setmKeywords(key);
            list.add(item);
        }
    }
}

class JsonImage {
    List<String> ImageList;
}

class JsonType {
    String pageSize;
    int total;
    List<JsonNews> data;
    String currentPage;
}

class JsonNews {
    String image;
    String publishTime;
    List<JsonKeyWordOrWhenOrWhereOrWho> keywords;
    String language;
    String video;
    String title;
    List<JsonKeyWordOrWhenOrWhereOrWho> when;
    String content;
    List<JsonPersonOrOrganization> persons;
    String newsID;
    String crawlTime;
    List<JsonPersonOrOrganization> Organizations;
    String publisher;
    List<location> locations;
    List<JsonKeyWordOrWhenOrWhereOrWho> where;
    String category;
    List<JsonKeyWordOrWhenOrWhereOrWho> who;
}

class JsonKeyWordOrWhenOrWhereOrWho {
    double score;
    String word;
}

class JsonPersonOrOrganization {
    int count;
    String linkedUrl;
    String mention;
}

class location {
    double lng;
    int count;
    String linkedURL;
    double lat;
    String mention;
}

