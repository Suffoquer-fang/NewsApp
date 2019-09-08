package com.example.Fangyan.Activity;

import android.annotation.SuppressLint;
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

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.Fangyan.Adapter.MyCursorAdapter;
import com.example.Fangyan.R;
import com.example.Fangyan.helper.MyDBOpenHelper;
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
        helper = MyDBOpenHelper.getInstance(this, "TEST1.db", null, 1);

        getSupportActionBar().hide();
        if (savedInstanceState == null) {
            initView();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView()
    {

        lv = findViewById(R.id.list);

        Cursor c = helper.query();



        mAdapter = new MyCursorAdapter(this, c, helper, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = ((TextView)view.findViewById(R.id.content)).getText().toString();

                sv.setQuery(tmp, true);
            }
        });



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
                        onBackPressed();
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
    protected void onResume() {
        super.onResume();
        Cursor c = helper.query();
        mAdapter.changeCursor(c);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);

    }
}
