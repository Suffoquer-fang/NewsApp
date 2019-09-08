package com.example.Fangyan.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseLazyLoadFragment extends Fragment {
    public boolean isPrepared;
    public boolean isLazyLoaded;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        lazyload();
    }

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = initView(inflater, container);//让子类实现初始化视图

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyload();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLazyLoaded = false;
    }

    private void lazyload()
    {
        if(getUserVisibleHint() && isPrepared && !isLazyLoaded)
        {
            onLazyLoad();
            isLazyLoaded = true;
        }
    }

    //数据加载接口，留给子类实现
    public abstract boolean onLazyLoad();

    //初始化视图接口，子类必须实现
    public abstract View initView(LayoutInflater inflater, @Nullable ViewGroup container);

}
