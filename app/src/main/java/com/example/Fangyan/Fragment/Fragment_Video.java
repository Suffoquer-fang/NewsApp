package com.example.Fangyan.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.Fangyan.Activity.NewsDetailActivity;
import com.example.Fangyan.Adapter.NewsItemRecyclerViewAdapter;
import com.example.Fangyan.R;
import com.example.Fangyan.dummy.NewsItem;
import com.example.Fangyan.dummy.UrlHelper;
import com.example.Fangyan.helper.GetNewsHelper;
import com.example.Fangyan.helper.TimeHelper;
import com.github.nukc.stateview.StateView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import es.dmoral.toasty.Toasty;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * a simple {@link Fragment} subclass.
 * Use the {@link Fragment_Video#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Video extends Fragment
implements BGARefreshLayout.BGARefreshLayoutDelegate{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
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

    private String currFirstTime = "";
    private String currLastTime = "";

    private String[] videoList = new String[]{
            "http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4",
            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4",
            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
            "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
            "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4",
            "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
            "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4",
            "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4"
    };



    public Fragment_Video() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return a new instance of fragment Fragment_Video.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Video newInstance(String param1, String param2) {
        Fragment_Video fragment = new Fragment_Video();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment__video, container, false);

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

        return view;
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
        bgaRefreshLayout.setEnabled(false);
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
                    return;
                }
//                getNewsHelper.addHistory(newsItemList.get(position));
                adapter.notifyItemChanged(position);

                Toast.makeText(getContext(), newsItemList.get(position).getmTitle(), Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("content", content);
                bundle.putStringArrayList("imgs", arrimgs);
                bundle.putString("ID", newsID);
                intent.putExtras(bundle);
                startActivity(intent);



                Animatoo.animateSlideLeft(getContext());
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        requestNewsData();
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void requestNewsData()
    {
        newsItemList.clear();
        for(int i = 0; i < videoList.length; ++i) {
            NewsItem item = new NewsItem("Video");
            item.setmNewsID("video" + i);
            item.setmType(3);
            item.setInFavorite(false);
            item.setInHistory(false);
            List<String> keys = new ArrayList<>();
            keys.add("视频");
            item.setmKeywords(keys);
            item.setmVideo(videoList[i]);
            item.setmChannel("视频");
            item.setmAuthor("");
            item.setmImages(null);
            item.setmContent("这是一条视频新闻");
            item.setmPubTime(TimeHelper.getCurrentTime());
            newsItemList.add(item);
        }

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                bgaRefreshLayout.endRefreshing();
                stateView.showContent();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            //相当于OnResume(),可以做相关逻辑
        }else {
            //相当于OnPause()
            System.out.println("bukejian");
            onPause();
        }
    }



    @Override
    public void onBGARefreshLayoutBeginRefreshing(final BGARefreshLayout refreshLayout) {

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                bgaRefreshLayout.endRefreshing();
                Toasty.success(recyclerView.getContext(), "刷新成功", Toasty.LENGTH_SHORT, true).show();
            }
        }, 1000);

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
