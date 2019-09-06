//package com.example.newsapplication.Fragment;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.SearchView;
//import android.widget.TextView;
//
//import androidx.fragment.app.DialogFragment;
//
//import com.example.newsapplication.Adapter.MyCursorAdapter;
//import com.example.newsapplication.R;
//import com.example.newsapplication.helper.MyDBOpenHelper;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//
//
//public class HisFragment extends DialogFragment {
//
//    private static final String ARG_COLUMN_COUNT = "column-count";
//    private ListView lv;
//    private TextView Cancel;
//    private TextView Clear;
//    private SearchView sv;
//    private MyCursorAdapter mAdapter;
//    private int mColumnCount = 1;
//    private MyDBOpenHelper helper;
//
//    public HisFragment() {
//    }
//
//    public static HisFragment newInstance(int columnCount) {
//        HisFragment fragment = new HisFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
//        helper = new MyDBOpenHelper(getContext(), "TEST1.db", null, 1);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_his_list, container, false);
//
//        lv = view.findViewById(R.id.list);
//
//        Cursor c = helper.getReadableDatabase().query(true, "His", new String[]{"_id", "Content"}, null, null,null, null, "Time DESC", null);
//
//        c.moveToFirst();
//        mAdapter = new MyCursorAdapter(lv.getContext(), c, helper);
//        lv.setAdapter(mAdapter);
//
//        Cancel = view.findViewById(R.id.textView);
//        Clear = view.findViewById(R.id.textView2);
//        sv = view.findViewById(R.id.search);
//
//        Cancel.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        Cancel.setTextColor(getContext().getResources().getColor(R.color.SlightBlue));
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP: {
//
//                        Cancel.setTextColor(getContext().getResources().getColor(R.color.DeepBlue));
//                        dismiss();
//                        break;
//                    }
//                }
//                return false;
//            }
//        });
//
//
//        Clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAdapter.clear();
//            }
//        });
//
//
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                //TODO Query
//
//                ContentValues cv = new ContentValues();
//                SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
//                cv.put("Time", ft.format(new Date()));
//                cv.put("Content", sv.getQuery().toString());
//                helper.insertHis(cv);
//                Log.d("insert", sv.getQuery().toString());
//                SQLiteDatabase rdb = helper.getReadableDatabase();
//                Cursor c = rdb.query(true, "His", new String[]{"_id", "Content"}, null, null,null, null, "Time DESC", null);
//                mAdapter.changeCursor(c);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//
//        return view;
//    }
//
//}
