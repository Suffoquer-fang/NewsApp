package com.example.newsapplication.Adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapplication.Fragment.HisFragment;
import com.example.newsapplication.R;

import java.util.List;

public class MyHisRecyclerViewAdapter extends RecyclerView.Adapter<MyHisRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;

    public MyHisRecyclerViewAdapter(List<String> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_his, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void add(String str){
        if (mValues.contains(str)) return;
        mValues.add(0, str);
        notifyItemInserted(0);
    }

    public void Clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(String str){
        int index = mValues.indexOf(str);
        mValues.remove(index);
        notifyItemRemoved(index);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final MyHisRecyclerViewAdapter adapter;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view, final MyHisRecyclerViewAdapter ad) {
            super(view);
            adapter = ad;
            mView = view;
            mContentView = view.findViewById(R.id.content);
            final ImageView v = view.findViewById(R.id.close);
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            break;
                        }
                        case MotionEvent.ACTION_UP:{
                            ad.remove(mContentView.getText().toString());
                            //TODO remove single his
                            break;
                        }
                    }

                    return false;
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "";
        }
    }
}
