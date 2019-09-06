package com.example.newsapplication.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dalimao.library.util.FloatUtil;
import com.example.newsapplication.R;
import com.example.newsapplication.helper.ShareUtil;
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class NewsDetailActivity extends AppCompatActivity {
    private Slidr slidr;
    private ImageView imageView;
    private SparkButton favourite;
    private SparkButton share;
    private ShareUtil shareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        shareUtil = new ShareUtil(this);

        favourite = findViewById(R.id.like);
        share = findViewById(R.id.share);
//        favourite.setEventListener(new SparkEventListener() {
//            @Override
//            public void onEvent(ImageView button, boolean buttonState) {
//
//            }
//
//            @Override
//            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
//
//            }
//
//            @Override
//            public void onEventAnimationStart(ImageView button, boolean buttonState) {
//
//            }
//        } {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        imageView.setOnClickListener(new View.OnClickListener() {
//                                         @Override
//                                         public void onClick(View view) {
//                                             share();
//                                         }
//                                     }
//        );


        slidr = new Slidr();

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true | false)
                .edgeSize(0.18f)
                .build();
        slidr.attach(this, config);


        Bundle bundle = getIntent().getExtras();

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
}
