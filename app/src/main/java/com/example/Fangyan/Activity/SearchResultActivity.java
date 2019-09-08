package com.example.Fangyan.Activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.Fangyan.Adapter.NewsItemRecyclerViewAdapter;
import com.example.Fangyan.R;
import com.example.Fangyan.dummy.NewsItem;
import com.example.Fangyan.helper.GetNewsHelper;
import com.example.Fangyan.helper.GetNewsListener;
import com.example.Fangyan.helper.TimeHelper;
import com.github.nukc.stateview.StateView;
import com.r0adkll.slidr.Slidr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import es.dmoral.toasty.Toasty;

public class SearchResultActivity extends AppCompatActivity
        implements BGARefreshLayout.BGARefreshLayoutDelegate, GetNewsListener {


    private RecyclerView recyclerView = null;
    private NewsItemRecyclerViewAdapter adapter;
    private NewsItemRecyclerViewAdapter.ItemClickListener itemClickListener;

    private List<NewsItem> newsItemList = new ArrayList<>();
    private BGARefreshLayout bgaRefreshLayout;
    private StateView stateView;

    private GetNewsHelper getNewsHelper = new GetNewsHelper(this);

    private Slidr slidr;

    private TextView Cancel;
    private SearchView searchView;

    private String searchKeyword;

    private int lastPosition = 0;
    private int lastOffset = 0;

    private boolean isReadyForSearch = false;


    private String currFirstTime = "";
    private String currLastTime = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getNewsHelper.setSearchResultListener(this);

        slidr = new Slidr();
        slidr.attach(this);

        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        searchKeyword = bundle.getString("SearchKey");

        initView();
    }

    public void initView()
    {
        initListener();

        recyclerView = findViewById(R.id.search_result_recyclerView);
        adapter = new NewsItemRecyclerViewAdapter(newsItemList, itemClickListener);

        Cancel = findViewById(R.id.textView);
        searchView = findViewById(R.id.search);

        bgaRefreshLayout = findViewById(R.id.BGARefreshLayout);
        stateView = StateView.wrap(bgaRefreshLayout);

        stateView.setLoadingResource(R.layout.centerloading);
        stateView.showLoading();


        initRecyclerView();
        initSwipe();

        bgaRefreshLayout.setPullDownRefreshEnable(false);
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


        Cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Cancel.setTextColor(getApplicationContext().getResources().getColor(R.color.SlightBlue));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {

                        Cancel.setTextColor(getApplicationContext().getResources().getColor(R.color.DeepBlue));
                        onBackPressed();
                        break;
                    }
                }
                return false;
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO Query

                ContentValues cv = new ContentValues();
                SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
                cv.put("Time", ft.format(new Date()));
                cv.put("Content", searchView.getQuery().toString());
                getNewsHelper.getDBhelper().insertHis(cv);
                Log.d("insert", searchView.getQuery().toString());

                searchKeyword = s;
                if(isReadyForSearch)
                    updateSearchResult();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        searchView.setQuery(searchKeyword, false);
        updateSearchResult();

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
    }

    public void initListener()
    {
        itemClickListener = new NewsItemRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isDel) {
                if(isDel)
                {
                    newsItemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, newsItemList.size() - position);//通知重新绑定某一范围内的的数据与界面
                }
                else
                    clickItem(position);


            }
        };
    }

    public void updateSearchResult()
    {
        currFirstTime = "";
        currLastTime = "";
        newsItemList.clear();
        adapter.setKeyword(searchKeyword);
        adapter.notifyDataSetChanged();
        stateView.showLoading();
        requestNewsData();
    }


    public void requestNewsData()
    {
        getNewsHelper.getNewsFromPureNetwork("", currFirstTime, "", searchKeyword, this, false);
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        requestNewsData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getNewsHelper.getNewsFromPureNetwork("", "", currLastTime, searchKeyword, this, true);
        return true;
    }


    @Override
    public void onGetNewsSuccessful(List<NewsItem> newsList, boolean loadmore) {
        if(!loadmore) {

            newsItemList.addAll(0, newsList);

            final int addSize = newsList.size();

            if(newsItemList.size() > 0) {
                currFirstTime = newsItemList.get(0).getmPubTime();
                currFirstTime = TimeHelper.timeAfter(currFirstTime, 1);
                currLastTime = newsItemList.get(newsItemList.size()-1).getmPubTime();
                currLastTime = TimeHelper.timeBefore(currLastTime, 1);
            }

            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    adapter.notifyDataSetChanged();
                    bgaRefreshLayout.endRefreshing();
                    stateView.showContent();
                    isReadyForSearch = true;
                    String msg = "为您找到了"+(addSize)+"条搜索结果";
                    Toasty.success(recyclerView.getContext(), msg, Toast.LENGTH_SHORT, true).show();
                }
            }, 500);
        }
        else {
            newsItemList.addAll(newsList);
            if(newsItemList.size() > 0) {
                currLastTime = newsItemList.get(newsItemList.size()-1).getmPubTime();
                currLastTime = TimeHelper.timeBefore(currLastTime, 1);
            }
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isReadyForSearch = true;
                    adapter.notifyDataSetChanged();
                    bgaRefreshLayout.endLoadingMore();
                    stateView.showContent();
                }
            }, 500);
        }


    }

    @Override
    public void onGetNewsFailed(int failed_id) {
        System.out.println("failwww:" + failed_id);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                stateView.showContent();
                bgaRefreshLayout.endLoadingMore();
                bgaRefreshLayout.endRefreshing();
                isReadyForSearch = true;
                Toasty.error(recyclerView.getContext(), "网络请求失败", Toasty.LENGTH_SHORT, true).show();

            }
        });

    }


    public void clickItem(int position)
    {
        getNewsHelper.addHistory(newsItemList.get(position));
        adapter.notifyItemChanged(position);

        String title = newsItemList.get(position).getmTitle();
        String content = newsItemList.get(position).getmContent();
        List<String> imgs= newsItemList.get(position).getmImages();
        ArrayList<String> arrimgs = new ArrayList<>();

        String newsID = newsItemList.get(position).getmNewsID();


        getNewsHelper.addNewKeyword(newsItemList.get(position).getmKeywords().get(0));


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

}
