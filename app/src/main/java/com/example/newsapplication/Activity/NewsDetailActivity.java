package com.example.newsapplication.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.newsapplication.R;
import com.example.newsapplication.helper.ShareUtil;
import com.qmuiteam.qmui.widget.textview.QMUISpanTouchFixTextView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.ArrayList;

public class NewsDetailActivity extends AppCompatActivity {
    private Slidr slidr;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        final ShareUtil shareUtil = new ShareUtil(this);

        imageView = findViewById(R.id.share);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand = (int)Math.random() * 4;
                switch (rand) {
                    // qq&文字
                    case 0:
                        shareUtil.shareText("com.tencent.mobileqq", null, "这是一条分享信息",
                                "分享标题", "分享主题");
                        break;
                    // 微信&文字
                    case 1:
                        shareUtil.shareText("com.tencent.mm", null, "这是一条分享信息", "分享标题",
                                "分享主题");
                        break;
                    // 所有&文字
                    case 2:
                        shareUtil.shareText(null, null, "这是一条分享信息", "分享标题", "分享主题");
                        break;
                    // 微信朋友&文字
                    case 3:
                        if (shareUtil.checkInstall("com.tencent.mm")) {
                            shareUtil.shareText("com.tencent.mm",
                                    "com.tencent.mm.ui.tools.ShareImgUI",
                                    "https://www.aiipu.com/", "分享标题", "分享主题");
                        } else {
                            shareUtil.toInstallWebView("https://weixin.qq.com/download");
                        }
                        break;
                    // qq朋友&文字
                    case 4:
                        if (shareUtil.checkInstall("com.tencent.mobileqq")) {
                            shareUtil.shareText("com.tencent.mobileqq",
                                    "com.tencent.mobileqq.activity.JumpActivity",
                                    "https://www.aiipu.com/", "分享标题", "分享主题");
                        } else {
                            shareUtil.toInstallWebView("https://im.qq.com/mobileqq/");
                        }
                        break;
                }
            }
        });


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
        if (list != null)
            for (String url : list) {
                ImageView imageView = new ImageView(this);

                Glide.with(this).load(url).into(imageView);

                linearLayout.addView(imageView);
            }

        ((TextView) findViewById(R.id.Title_textView)).setText(bundle.getString("title"));
        ((QMUISpanTouchFixTextView) findViewById(R.id.Contet_textView)).setText(bundle.getString("content"));

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

    public void shareText() {

    }
}
