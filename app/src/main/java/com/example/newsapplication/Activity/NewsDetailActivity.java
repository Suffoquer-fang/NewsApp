package com.example.newsapplication.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.newsapplication.R;
import com.example.newsapplication.dummy.NewsItem;
import com.example.newsapplication.helper.GetNewsHelper;
import com.example.newsapplication.helper.ShareAnyWhere;
import com.example.newsapplication.helper.ShareUtil;
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;
import com.r0adkll.slidr.Slidr;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;

public class NewsDetailActivity extends AppCompatActivity {
    private Slidr slidr;
    private ImageView imageView;
    private SparkButton favourite;
    private SparkButton share;
    private ShareUtil shareUtil;
    private ShareAnyWhere shareAnyWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        shareUtil = new ShareUtil(this);

        favourite = findViewById(R.id.like);
        share = findViewById(R.id.share);
        shareAnyWhere = new ShareAnyWhere();
        setTitle("JARSA NEWS");


        final Bundle bundle = getIntent().getExtras();

        final String id = bundle.getString("ID");


        boolean fav = new GetNewsHelper(getApplicationContext()).isInFavorite(id);

        favourite.setChecked(fav);

        System.out.println("isFav" +  fav);

        favourite.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                NewsItem item = new NewsItem("sds");
                item.setmNewsID(id);
                if(buttonState)
                    new GetNewsHelper(getApplicationContext()).addFavorite(item);
                else
                    new GetNewsHelper(getApplicationContext()).deleteFavorite(item);
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });


        slidr = new Slidr();

        slidr.attach(this);



        LinearLayout linearLayout = findViewById(R.id.linear);

        ArrayList<String> list = bundle.getStringArrayList("imgs");


        String text = bundle.getString("content");
        text = text.replaceAll(" ", "").replaceAll(String.valueOf(((char) 12288)), "").replaceAll("\\n+", "\n");
        text = "        " + text.replaceAll("返回搜狐.*", "").replaceAll("责任编辑：", "");
        text = text.replaceAll("\\n", "\n        ").replaceAll(".*原标题.*\\n", "");
        String[] texts = text.split("\\n");

        if (list != null) {
            int cnt, len, part, index;
            StringBuilder stringBuilder = new StringBuilder();
            cnt = list.size();
            len = text.length();
            part = len / cnt;
            cnt = 0;
            index = 0;
            for (String i : texts) {
                cnt += i.length();
                stringBuilder.append(i);
                stringBuilder.append("\n");
                if (stringBuilder.length() > part) {
                    QMUISpanTouchFixTextView textView = new QMUISpanTouchFixTextView(this);
                    textView.setText(stringBuilder.toString());

                    stringBuilder.setLength(0);
                    textView.setEms(10);
                    textView.setTextSize(20);
                    ImageView imageView = new ImageView(this);
                    Glide.with(this).load(list.get(index)).into(imageView);
                    imageView.setPadding(0, 5, 0, 5);
                    linearLayout.addView(textView);
                    linearLayout.addView(imageView);

                    index++;
                }
            }
            if (index == list.size() - 1) {
                ImageView imageView = new ImageView(this);

                Glide.with(this).load(list.get(index)).into(imageView);

                linearLayout.addView(imageView);

            }
        }
        else {
            QMUISpanTouchFixTextView textView = new QMUISpanTouchFixTextView(this);
            textView.setText(text);
            textView.setEms(10);
            textView.setTextSize(20);
            linearLayout.addView(textView);
        }

        ((TextView)

                findViewById(R.id.Title_textView)).

                setText(bundle.getString("title"));


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        final String finalText = "标题: " + bundle.getString("title") + "\n\n" + text;



        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //shareUtil.shareImg();
                //shareUtil.shareText("com.tencent.mm", null, finalText, bundle.getString("title"), "分享新闻");
                //shareUtil.shareText("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI", "https://www.aiipu.com/", "分享标题", "分享主题");
                shareUtil.shareText("com.tencent.mobileqq", null, finalText, bundle.getString("title"), "分享新闻");
                //shareUtil.shareImg("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI", uri);

            }
        });












    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void share() {
//
//    }
//
//
//    public void shareQQ() {
//        shareUtil.shareText("com.tencent.mobileqq", null, "这是一条分享信息",
//                "分享标题", "分享主题");
//    }
//
//    public void shareWX() {
//        shareUtil.shareText("com.tencent.mm", null, "这是一条分享信息", "分享标题",
//                "分享主题");
//    }
    public void shareText() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }


    public void share(Uri uri) {
        shareAnyWhere.shareQQ(this, shareAnyWhere.createUriList(uri), "日你先人");
    }

}
