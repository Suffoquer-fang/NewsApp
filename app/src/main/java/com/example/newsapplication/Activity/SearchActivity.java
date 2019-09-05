package com.example.newsapplication.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapplication.Adapter.MyCursorAdapter;
import com.example.newsapplication.R;
import com.example.newsapplication.helper.MyDBOpenHelper;
import com.r0adkll.slidr.Slidr;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SearchActivity extends AppCompatActivity {

    private Slidr slidr;
    private ListView lv;
    private TextView Cancel;
    private TextView Clear;
    private SearchView sv;
    private MyCursorAdapter mAdapter;
    private MyDBOpenHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        slidr = new Slidr();
        slidr.attach(this);
        helper = new MyDBOpenHelper(this, "TEST1.db", null, 1);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            initView();
        }
    }

    public void initView()
    {

        lv = findViewById(R.id.list);

        Cursor c = helper.query();

        mAdapter = new MyCursorAdapter(this, c, helper);
        lv.setAdapter(mAdapter);

        Cancel = findViewById(R.id.textView);
        Clear = findViewById(R.id.textView2);
        sv = findViewById(R.id.search);

        Cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Cancel.setTextColor(getApplicationContext().getResources().getColor(R.color.SlightBlue));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {

                        Cancel.setTextColor(getApplicationContext().getResources().getColor(R.color.DeepBlue));
                        finish();
                        break;
                    }
                }
                return false;
            }
        });


        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.clear();
            }
        });


        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO Query

                ContentValues cv = new ContentValues();
                SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
                cv.put("Time", ft.format(new Date()));
                cv.put("Content", sv.getQuery().toString());
                helper.insertHis(cv);
                Log.d("insert", sv.getQuery().toString());
                mAdapter.changeCursor(helper.query());

                getSearchResult(sv.getQuery().toString());

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void getSearchResult(String s)
    {
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, NewsListFragment.newInstance("科技", "")).commitNow();

        Intent intent = new Intent(this, SearchResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("SearchKey", s);
        intent.putExtras(bundle);
        startActivity(intent);



    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }
}
