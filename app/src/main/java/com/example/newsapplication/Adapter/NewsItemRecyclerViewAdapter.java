package com.example.newsapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapplication.R;
import com.example.newsapplication.dummy.NewsItem;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

public class NewsItemRecyclerViewAdapter extends RecyclerView.Adapter<NewsItemRecyclerViewAdapter.ViewHolder> {

    private final List<NewsItem> mNewsList;
    private final ItemClickListener mListener;

    public boolean isSwipe() {
        return isSwipe;
    }

    public void setSwipe(boolean swipe) {
        isSwipe = swipe;
    }

    private boolean isSwipe = false;

    public interface ItemClickListener{
        void onItemClick(int position, boolean isDel);
    }

    public NewsItemRecyclerViewAdapter(List<NewsItem> items, ItemClickListener listener) {
        mNewsList = items;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mNewsList.get(position).getmType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //System.out.println("create");
        switch (viewType)
        {
            case 0:
                return new NoPhotoViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_type_0, parent, false));
            case 1:
                return new SinglePhotoViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_type_1, parent, false));
            case 2:
                return new MultiPhotosViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_type_2, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mNewsList.get(position);
        //System.out.println("bind");
        holder.updateView();


        ((SwipeMenuLayout)holder.mView).setSwipeEnable(isSwipe);

        if(isSwipe)
        {
            holder.mView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(position, true);
                }
            });
        }

        holder.mView.findViewById(R.id.content_fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemClick(position, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }



    abstract class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mPubTimeView;
        public final TextView mAuthorView;

        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTitleView = (TextView)view.findViewById(R.id.title);
            mPubTimeView = (TextView)view.findViewById(R.id.pubtime);
            mAuthorView = (TextView)view.findViewById(R.id.author);

        }
        abstract public void updateView();
    }


    public class NoPhotoViewHolder extends ViewHolder
    {
        public NoPhotoViewHolder(View view) {super(view);}
        public void updateView() {
            mAuthorView.setText(mItem.getmAuthor());
            mTitleView.setText(mItem.getmTitle());
            mPubTimeView.setText(mItem.getmPubTime());

        }
    }

    public class SinglePhotoViewHolder extends ViewHolder
    {
        public final ImageView mImgView;
        public SinglePhotoViewHolder(View view)
        {
            super(view);
            mImgView = (ImageView)view.findViewById(R.id.image_type_1);
        }
        public void updateView()
        {
            mAuthorView.setText(mItem.getmAuthor());
            mTitleView.setText(mItem.getmTitle());
            mPubTimeView.setText(mItem.getmPubTime());
            mImgView.setImageResource(R.drawable.ic_menu_camera); //TODO
            Glide.with(mView).load(mItem.getmImages().get(0)).into(mImgView);
        }
    }

    public class MultiPhotosViewHolder extends ViewHolder
    {
        public final ImageView mImgView_1, mImgView_2, mImgView_3;
        public MultiPhotosViewHolder(View view)
        {
            super(view);
            mImgView_1 = (ImageView)view.findViewById(R.id.first_image_type_2);
            mImgView_2 = (ImageView)view.findViewById(R.id.second_image_type_2);
            mImgView_3 = (ImageView)view.findViewById(R.id.third_image_type_2);
        }
        public void updateView()
        {
            mTitleView.setText(mItem.getmTitle());
            mPubTimeView.setText(mItem.getmPubTime());
            mAuthorView.setText(mItem.getmAuthor());
            mImgView_2.setImageResource(R.drawable.ic_menu_camera);
            mImgView_1.setImageResource(R.drawable.ic_menu_gallery);
            mImgView_3.setImageResource(R.drawable.ic_menu_send); // TODO

            Glide.with(mView).load(mItem.getmImages().get(0)).into(mImgView_1);
            Glide.with(mView).load(mItem.getmImages().get(1)).into(mImgView_2);
            Glide.with(mView).load(mItem.getmImages().get(2)).into(mImgView_3);
        }
    }





}