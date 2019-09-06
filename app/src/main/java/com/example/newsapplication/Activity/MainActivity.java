package com.example.newsapplication.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.newsapplication.Adapter.MyFragmentPagerAdapter;
import com.example.newsapplication.Fragment.Fragment_Info;
import com.example.newsapplication.Fragment.Fragment_Video;
import com.example.newsapplication.Fragment.HomeFragment;
import com.example.newsapplication.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);




        initViewPager();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initFragmentList()
    {
        fragmentList = new ArrayList<>();
        fragmentList.add(HomeFragment.newInstance("首页"));

        fragmentList.add(Fragment_Video.newInstance("视频", ""));

        fragmentList.add(Fragment_Info.newInstance("个人", ""));
        //fragmentList.add(HisFragment.newInstance(1));
        //TODO
        //ADD 首页，视频，个人，（搜索）
    }

    public void initViewPager()
    {
        String[] titles = getResources().getStringArray(R.array.tab_title);
        initFragmentList();
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(fragmentList.size());
        FragmentStatePagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(adapter);

        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        for(int i = 0; i < mTabLayout.getTabCount(); ++i)
        {
            selectTab(mTabLayout.getTabAt(i), mTabLayout.getTabAt(i).isSelected());
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //DO NOTHING
            }
        });

    }

    public void selectTab(TabLayout.Tab tab, boolean selected)
    {
        View view = tab.getCustomView();
        if(view == null)
        {
            //tab.setCustomView(R.layout.fragment_item_0);
            view = tab.getCustomView();
        }


        if(selected)
        {
            //TODO
        }
        else
        {
            //TODO
        }
    }
    @Override
    public void finish()
    {
        System.out.println("finish");
        moveTaskToBack(true);
    }


}
