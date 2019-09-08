package com.example.Fangyan.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager manager;
    private List<Fragment> fragmentList;
    private String[] pageTitles;


    public MyFragmentPagerAdapter(FragmentManager m, List<Fragment> list, String[] titles)
    {
        super(m);
        manager = m;
        fragmentList = list;
        pageTitles = titles;
    }

    public void setPageTitles(String[] titles)
    {
        pageTitles = titles;
    }

    public void setFragmentList(List<Fragment> list)
    {
//        if(this.fragmentList != null){
//            FragmentTransaction ft = manager.beginTransaction();
//            for(Fragment f:this.fragmentList){
//                ft.remove(f);
//            }
//            ft.commit();
//            ft=null;
//            manager.executePendingTransactions();
//        }
        fragmentList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
