package com.example.Fangyan.helper;

import androidx.recyclerview.widget.DiffUtil;

import com.example.Fangyan.dummy.NewsItem;

import java.util.List;

public class MyDiffCallback extends DiffUtil.Callback {
    //Thing 是adapter 的数据类，要换成自己的adapter 数据类
    private List<NewsItem> current;
    private List<NewsItem> next;

    public MyDiffCallback(List<NewsItem> current, List<NewsItem> next) {
        this.current = current;
        this.next = next;

    }

    /**
     * 旧数据的size
     */
    @Override
    public int getOldListSize() {
        return current.size();
    }

    /**
     * 新数据的size
     */
    @Override
    public int getNewListSize() {
        return next.size();
    }

    /**
     * 这个方法自由定制 ，
     * 在对比数据的时候会被调用
     * 返回 true 被判断为同一个item
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        NewsItem currentItem = current.get(oldItemPosition);
        NewsItem nextItem = next.get(newItemPosition);

        return currentItem.getmNewsID().equals(nextItem.getmNewsID());
    }

    /**
     *在上面的方法返回true 时，
     * 这个方法才会被diff 调用
     * 返回true 就证明内容相同
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        NewsItem currentItem = current.get(oldItemPosition);
        NewsItem nextItem = next.get(newItemPosition);
        return currentItem.equals(nextItem);
    }
}