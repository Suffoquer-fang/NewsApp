package com.example.newsapplication.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.newsapplication.Fragment.NewsListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> titles;
    private Context context;
    private Fragment mCurrentFragment;
    private int id=1;
    private Map<String,Integer> IdsMap=new HashMap<>();
    private List<String> preIds=new ArrayList<>();

    public DynamicFragmentPagerAdapter(Context context, FragmentManager fm, List<String> titles) {
        super(fm);
        this.context = context;
        this.titles = titles;
        for(String title : titles){
            if(!IdsMap.containsKey(title)){
                IdsMap.put(title,id++);
            }
        }
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("category", titles.get(position));
        NewsListFragment columnFragment = new NewsListFragment();
        columnFragment.setArguments(bundle);
        return columnFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public long getItemId(int position) {
        if(getPageTitle(position)!=null){
            return IdsMap.get(getPageTitle(position));
        }
        return 0;
    }

    @Override
    public int getItemPosition(Object object) {
        NewsListFragment fragment= (NewsListFragment) object;
        int preId = preIds.indexOf(fragment.getArgCategory());
        int newId=-1;
        int i=0;
        int size=getCount();
        for(;i<size;i++){
            if(getPageTitle(i).equals(fragment.getArgCategory())){
                newId=i;
                break;
            }
        }
        if(newId!=-1&&newId==preId){
            return POSITION_UNCHANGED;
        }
        if(newId!=-1){
            return newId;
        }
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        for(String title :titles){
            if(!IdsMap.containsKey(title)){
                IdsMap.put(title,id++);
            }
        }
        super.notifyDataSetChanged();
        preIds.clear();
        int size=getCount();
        for(int i=0;i<size;i++){
            preIds.add((String) getPageTitle(i));
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (Fragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

}