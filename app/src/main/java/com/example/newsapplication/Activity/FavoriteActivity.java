package com.example.newsapplication.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.newsapplication.Adapter.NewsItemRecyclerViewAdapter;
import com.example.newsapplication.R;
import com.example.newsapplication.dummy.NewsItem;
import com.example.newsapplication.helper.GetNewsHelper;
import com.example.newsapplication.helper.GetNewsListener;
import com.github.nukc.stateview.StateView;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class FavoriteActivity extends AppCompatActivity
        implements BGARefreshLayout.BGARefreshLayoutDelegate, GetNewsListener {

    private RecyclerView recyclerView = null;
    private NewsItemRecyclerViewAdapter adapter;
    private NewsItemRecyclerViewAdapter.ItemClickListener itemClickListener;
    private List<NewsItem> newsItemList = new ArrayList<>();

    private StateView stateView;
    private BGARefreshLayout bgaRefreshLayout;
    private GetNewsHelper getNewsHelper = new GetNewsHelper(this);

    private Slidr slidr;


    private int lastPosition = 0;
    private int lastOffset = 0;

    boolean isLoading = false;

    int retryTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getNewsHelper.setFavoriteListener(this);
        slidr = new Slidr();
        slidr.attach(this);

        setTitle("JARSA NEWS 新闻收藏");
        initView();
    }

    public void initView()
    {
        initListener();

        recyclerView = findViewById(R.id.fav_recyclerView);
        adapter = new NewsItemRecyclerViewAdapter(newsItemList, itemClickListener);
        adapter.setSwipe(true);

        bgaRefreshLayout = findViewById(R.id.BGARefreshLayout);
        stateView = StateView.wrap(recyclerView);

        stateView.setLoadingResource(R.layout.centerloading);


        initRecyclerView();
        initSwipe();

        if(newsItemList.size() == 0) {

            stateView.showLoading();
            //requestNewsData();
            bgaRefreshLayout.beginRefreshing();
            isLoading = true;
        }
    }




    @SuppressLint("ClickableViewAccessibility")
    public void initRecyclerView()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.getLayoutManager() != null) {
                    getPositionAndOffset();
                }
            }
        });
        if(recyclerView.getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }


    }

    private void getPositionAndOffset()
    {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        View topView = layoutManager.getChildAt(0);
        if(topView != null) {
            lastOffset = topView.getTop();
            lastPosition = layoutManager.getPosition(topView);
        }
    }

    public void initSwipe()
    {
        bgaRefreshLayout.setDelegate(this);
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        bgaRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        bgaRefreshLayout.setIsShowLoadingMoreView(false);
    }

    public void initListener()
    {
        itemClickListener = new NewsItemRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isDel) {
                if(!isDel)
                    clickItem(position);
                else
                    deleteItem(position);
            }
        };
    }



    public void requestNewsData()
    {
        retryTime += 1;
        if(retryTime > 5) return;
        getNewsHelper.requestFavorite(10, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isLoading)
                {
                    System.out.println("still loading");
                    requestNewsData();
                }
            }
            }).start();

    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        requestNewsData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //getNewsHelper.requestSearchResult(30, "习近平");
        return true;
    }


    @Override
    public void onGetNewsSuccessful(final List<NewsItem> newsList, boolean loadmore) {

        System.out.println("get back succ");

        newsItemList.clear();
        newsItemList.addAll(0, newsList);
        if(!loadmore) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    bgaRefreshLayout.endRefreshing();
                    stateView.showContent();
                    isLoading = false;
                    retryTime = 0;
                }
            }, 500);
        }
        else {
            newsItemList.addAll(newsList);
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    adapter.notifyDataSetChanged();
                    bgaRefreshLayout.endLoadingMore();
                    stateView.showContent();
                    retryTime = 0;
                    isLoading = false;
                }
            }, 500);
        }



    }

    @Override
    public void onGetNewsFailed(int failed_id) {
        isLoading = false;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {

                stateView.showContent();
            }
        });

    }

    public void clickItem(int position)
    {
        String title = newsItemList.get(position).getmTitle();
        String content = newsItemList.get(position).getmContent();
        List<String> imgs= newsItemList.get(position).getmImages();
        ArrayList<String> arrimgs = new ArrayList<>();

        String newsID = newsItemList.get(position).getmNewsID();

        if (imgs == null)
            arrimgs = null;
        else
        {
            for(String a : imgs)
                arrimgs.add(a);
        }
        Intent intent = new Intent(this, NewsDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putStringArrayList("imgs", arrimgs);
        bundle.putString("ID", newsID);
        intent.putExtras(bundle);
        startActivity(intent);



        Animatoo.animateSlideLeft(this);
    }


    public void deleteItem(int position)
    {
        NewsItem tmp = newsItemList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, newsItemList.size() - position);//通知重新绑定某一范围内的的数据与界面

        getNewsHelper.deleteFavorite(tmp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

}
