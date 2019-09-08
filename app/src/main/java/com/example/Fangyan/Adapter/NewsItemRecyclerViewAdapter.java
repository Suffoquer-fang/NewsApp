package com.example.Fangyan.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Fangyan.R;
import com.example.Fangyan.dummy.NewsItem;
import com.example.Fangyan.helper.GetNewsHelper;
import com.liyi.highlight.HighlightTextView;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.zyyoona7.popup.EasyPopup;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class NewsItemRecyclerViewAdapter extends RecyclerView.Adapter<NewsItemRecyclerViewAdapter.ViewHolder> {

    private final List<NewsItem> mNewsList;
    private final ItemClickListener mListener;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private String keyword = "";

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
            case 3:
                return new VideoViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_type_3, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mNewsList.get(position);
        //System.out.println("bind");
        holder.updateView();


        ((SwipeMenuLayout)holder.mView.findViewById(R.id.swipeMenuLayout)).setSwipeEnable(isSwipe);

        if(isSwipe)
        {
            holder.mView.findViewById(R.id.close_btn).setVisibility(View.INVISIBLE);
            holder.mView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(position, true);
                }
            });
        }

        if(holder.mItem.getmType() != 3) {
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

        if(holder.mItem.getmType() != 3) {
            holder.mView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickCloseBtn(view, position, holder.mItem);

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }


    public void setTitle(HighlightTextView view, String title, boolean isInHis)
    {

        view.setText(title);
        int normalColor = (!isInHis || isSwipe) ? view.getResources().getColor(R.color.black) :view.getResources().getColor(R.color.gray);
        view.setTextColor(normalColor);
//        if(!keyword.equals(""))
//        {
//            view.addContent(title).addFontColorStyle(normalColor, 0, title.length()).addFontColorStyleByKey(view.getResources().getColor(R.color.red_500), keyword).build();
//        }
//        else
//            view.addContent(title).addFontColorStyle(normalColor, 0, title.length()).build();

    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final HighlightTextView mTitleView;
        public final TextView mPubTimeView;
        public final TextView mAuthorView;

        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTitleView = (HighlightTextView) view.findViewById(R.id.title);
            mPubTimeView = (TextView)view.findViewById(R.id.pubtime);
            mAuthorView = (TextView)view.findViewById(R.id.author);

        }
        abstract public void updateView();
    }


    public class NoPhotoViewHolder extends ViewHolder
    {
        public NoPhotoViewHolder(View view) {super(view);}
        @SuppressLint("ResourceAsColor")
        public void updateView() {
            mAuthorView.setText(mItem.getmAuthor());

            mPubTimeView.setText(mItem.getmPubTime());

            setTitle(mTitleView, mItem.getmTitle(), mItem.isInHistory());
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
        @SuppressLint("ResourceAsColor")
        public void updateView()
        {

            setTitle(mTitleView, mItem.getmTitle(), mItem.isInHistory());

            mAuthorView.setText(mItem.getmAuthor());
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
        @SuppressLint("ResourceAsColor")
        public void updateView()
        {

            setTitle(mTitleView, mItem.getmTitle(), mItem.isInHistory());
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


    public class VideoViewHolder extends ViewHolder
    {
        public JCVideoPlayerStandard jcVideoPlayerStandard;
        public VideoViewHolder(View view) {super(view); jcVideoPlayerStandard = view.findViewById(R.id.videoplayer);}
        @SuppressLint("ResourceAsColor")
        public void updateView() {
            mAuthorView.setText(mItem.getmAuthor());

            mPubTimeView.setText(mItem.getmPubTime());

            setTitle(mTitleView, mItem.getmTitle(), mItem.isInHistory());

            jcVideoPlayerStandard.setUp(mItem.getmVideo()
                    ,JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "Video Clip");
        }
    }



    public void clickCloseBtn(View v, final int pos, NewsItem item)
    {
        final EasyPopup popup = EasyPopup.create().setContentView(v.getContext(), R.layout.popup)
                .setAnimationStyle(R.style.QMUI_Animation)
                .setFocusAndOutsideEnable(true)
                .apply();

        LinearLayout ll = popup.findViewById(R.id.ll);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        List<String> tmp = item.getmKeywords();

        for (String i : tmp) {
            TextView tv = new TextView(v.getContext());
            tv.setLayoutParams(layoutParams);
            tv.setBackground(v.getResources().getDrawable(R.drawable.textview_border));
            tv.setText(i);
            tv.setTextSize(20);
            ll.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String key = ((TextView)view).getText().toString();
                    new GetNewsHelper(view.getContext()).addNewKeyword(key);
                    popup.dismiss();
                    mListener.onItemClick(pos, true);

                }
            });
        }

        popup.showAsDropDown(v);


    }

}