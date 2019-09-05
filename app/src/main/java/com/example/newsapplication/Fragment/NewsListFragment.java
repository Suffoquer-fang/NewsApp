package com.example.newsapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapplication.Activity.NewsDetailActivity;
import com.example.newsapplication.Adapter.NewsItemRecyclerViewAdapter;
import com.example.newsapplication.R;
import com.example.newsapplication.dummy.JsonHelper;
import com.example.newsapplication.dummy.NewsItem;
import com.example.newsapplication.dummy.UrlHelper;
import com.example.newsapplication.helper.GetNewsHelper;
import com.example.newsapplication.helper.GetNewsListener;
import com.github.nukc.stateview.StateView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class NewsListFragment extends BaseLazyLoadFragment
    implements  Callback, BGARefreshLayout.BGARefreshLayoutDelegate, GetNewsListener

{


    private static final String ARG_CATEGORY = "category";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mCategory;
    private String mParam2;



    private RecyclerView recyclerView = null;
    private NewsItemRecyclerViewAdapter adapter;
    private NewsItemRecyclerViewAdapter.ItemClickListener itemClickListener;

    private List<NewsItem> newsItemList = new ArrayList<>();
    private BGARefreshLayout bgaRefreshLayout;
    private StateView stateView;

    private UrlHelper urlHelper;
    private GetNewsHelper getNewsHelper;

    private int lastPosition = 0;
    private int lastOffset = 0;



    public NewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mCategory = args.getString(ARG_CATEGORY);
        mParam2 = args.getString(ARG_PARAM2);
    }

    public static NewsListFragment newInstance(String param1, String param2) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(ARG_CATEGORY);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public String getArgCategory() {return mCategory;}

    @Override
    public boolean onLazyLoad() {
        Log.d(TAG, "lazy update begin! " + mCategory);
        getNewsHelper =  new GetNewsHelper(getContext());
        getNewsHelper.setNewsListener(this);
        requestNewsData();


        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), mCategory, Toast.LENGTH_SHORT);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_fragment__category, container, false);

        initListener();
        //urlHelper = new UrlHelper(mCategory, null);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new NewsItemRecyclerViewAdapter(newsItemList, itemClickListener);

        bgaRefreshLayout = view.findViewById(R.id.BGARefreshLayout);
        stateView = StateView.inject(view);

        stateView.setLoadingResource(R.layout.centerloading);
        stateView.showLoading();


        initRecyclerView();
        initSwipe();

        Log.d(TAG, "update begin! " + mCategory);

        return view;
    }


    @Override
    public void onFailure(Call call, IOException e) {
        //Log.d(TAG, "update failure! " + mCategory);
        isLazyLoaded = false;
        bgaRefreshLayout.endRefreshing();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        Log.d(TAG, "update ! " + mCategory);
        String json = response.body().string();

        JsonHelper jh = new JsonHelper();
        jh.parse(json);



    }


    public void initRecyclerView()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

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
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新和上拉加载更多的风格
        bgaRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    public void initListener()
    {
        itemClickListener = new NewsItemRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isDel) {

                getNewsHelper.addHistory(newsItemList.get(position));
                adapter.notifyItemChanged(position);

                Toast.makeText(getContext(), newsItemList.get(position).getmTitle(), Toast.LENGTH_SHORT).show();
                String title = newsItemList.get(position).getmTitle();
                String content = newsItemList.get(position).getmContent();
                List<String> imgs= newsItemList.get(position).getmImages();
                ArrayList<String> arrimgs = new ArrayList<>();
                if (imgs == null)
                    arrimgs = null;
                else
                {
                    for(String a : imgs)
                        arrimgs.add(a);
                }
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("content", content);
                bundle.putStringArrayList("imgs", arrimgs);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
    }

    public void initLoadMoreListener()
    {

    }


    public void requestNewsData()
    {
        getNewsHelper.requestUpdateNews(15, mCategory);

    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        requestNewsData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getNewsHelper.requestLoadMoreNews(30, mCategory);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy "+ mCategory);

    }

    @Override
    public void onGetNewsSuccessful(List<NewsItem> newsList, boolean loadmore) {
        if(!loadmore) {
            newsItemList.addAll(0, newsList);
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    adapter.notifyDataSetChanged();
                    bgaRefreshLayout.endRefreshing();
                    stateView.showContent();
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
                }
            }, 500);
        }


    }

    @Override
    public void onGetNewsFailed(int failed_id) {
        //
    }
}
