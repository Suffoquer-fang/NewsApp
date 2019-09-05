package com.example.newsapplication.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.newsapplication.Activity.ChannelActivity;
import com.example.newsapplication.Activity.SearchActivity;
import com.example.newsapplication.Adapter.DynamicFragmentPagerAdapter;
import com.example.newsapplication.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseLazyLoadFragment

{

    private static final String ARG_TITLE = "title";

    private String mtitle;


    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private TabLayout tabLayout;
    private ImageView addChannelBtn;
    private TextView readSearchTv;

    private List<Fragment> categoryList = new ArrayList<>();
    private String[] titles = null;

    private int lastCategory = 0;

    private List<String> alltitles = new ArrayList<>();




    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String title) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mtitle = getArguments().getString(ARG_TITLE);
        }
    }


    @Override
    public boolean onLazyLoad() {
        //Do Nothing
        return true;
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_fragment__home__page, container, false);

        viewPager = view.findViewById(R.id.categoryViewPager);
        tabLayout = view.findViewById(R.id.TabLayout);
        addChannelBtn = view.findViewById(R.id.addChannelButton);
        readSearchTv = view.findViewById(R.id.real_search_tv);

        initFragmentList();

        for(int i = 0; i < titles.length; ++i)
            alltitles.add(titles[i]);
        adapter = new DynamicFragmentPagerAdapter(getContext(), getChildFragmentManager(), alltitles);
        initViewPager();
        initTabLayout();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setCurrentItem(lastCategory);
        tabLayout.getTabAt(lastCategory).select();
    }



    public String getTitle()
    {
        return mtitle;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mtitle = args.getString(ARG_TITLE);
    }

    public void initFragmentList()
    {
        System.out.println("initFragment");
        categoryList.clear();

        if(titles == null)
            titles = getResources().getStringArray(R.array.category_tab_title);


        for(String title : titles)
        {
            categoryList.add(NewsListFragment.newInstance(title, ""));
        }
        //TODO
    }

    @SuppressLint("ResourceAsColor")
    public void initViewPager()
    {
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(categoryList.size());
    }

    public void initTabLayout()
    {
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setTabPaddingLeftAndRight(10, 10);
        tabLayout.setSelectedTabIndicatorHeight(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lastCategory = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
                slidingTabStrip.setMinimumWidth(slidingTabStrip.getMeasuredWidth() + addChannelBtn.getMeasuredWidth());
            }
        });

        addChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAddChannel(view);
            }
        });


        readSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSearch(view);
            }
        });
//
    }




    public void clickAddChannel(View view)
    {
        //new HisFragment().show(getChildFragmentManager(), "www");

        Intent intent = new Intent(getActivity(), ChannelActivity.class);

        Bundle bundle = new Bundle();

        ArrayList<String> channelList = new ArrayList<>();
        channelList.addAll(alltitles);

        bundle.putStringArrayList("channels", channelList);

        intent.putExtras(bundle);
        startActivityForResult(intent, 10008);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            System.out.println("null data");
        if(resultCode == 10008 && data != null)
        {
            System.out.println("getData");
            ArrayList<String> rtnChannels = data.getStringArrayListExtra("rtnChannels");
            if(rtnChannels == null) return;
            titles = new String[rtnChannels.size()];
            for(int i = 0; i < titles.length; ++i) {
                titles[i] = rtnChannels.get(i);
                System.out.println(titles[i]);
            }

            alltitles.clear();
            alltitles.addAll(rtnChannels);
            adapter.notifyDataSetChanged();


        }
    }
    public void clickSearch(View view)
    {
        //fragment1.show(getChildFragmentManager(), "sss");
        //new HisFragment().show(getChildFragmentManager(), "www");
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }
}
