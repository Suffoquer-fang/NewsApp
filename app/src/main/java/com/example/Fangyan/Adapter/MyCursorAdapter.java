package com.example.Fangyan.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Fangyan.R;
import com.example.Fangyan.helper.MyDBOpenHelper;


public class MyCursorAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;
    private final MyDBOpenHelper helper;
    private View.OnClickListener listener;

    public MyCursorAdapter(Context context, Cursor cursor, MyDBOpenHelper helper, View.OnClickListener listener) {
        super(context, cursor, 0);

        mInflater = LayoutInflater.from(context);
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.fragment_his, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView tv = view.findViewById(R.id.content);
        final ImageView iv = view.findViewById(R.id.close);

        tv.setText(cursor.getString(cursor.getColumnIndex("Content")));
        view.findViewById(R.id.content_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Shit", "Clicked close");
                helper.deleteHis(tv.getText().toString());
                changeCursor(helper.query());

                Log.d("Delete", tv.getText().toString());
            }
        });
    }

    @Override
    public void changeCursor(Cursor cursor) {
        
        super.changeCursor(cursor);
        notifyDataSetChanged();
    }

    public void clear() {
        helper.getWritableDatabase().execSQL("delete from SearchHis");
        changeCursor(null);
    }

}

