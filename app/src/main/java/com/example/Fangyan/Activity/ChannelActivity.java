package com.example.Fangyan.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.Fangyan.Adapter.ChannelAdapter;
import com.example.Fangyan.R;
import com.example.Fangyan.dummy.ChannelEntity;
import com.example.Fangyan.helper.ItemDragHelperCallback;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.ArrayList;
import java.util.List;


public class ChannelActivity extends Activity {

    private RecyclerView mRecy;
    private ImageView closeBtn;


    private List<ChannelEntity> items = new ArrayList<>();
    private List<ChannelEntity> otherItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);


        mRecy = (RecyclerView) findViewById(R.id.recy);
        closeBtn = findViewById(R.id.close_btn);

        Slidr slidr = new Slidr();
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .sensitivity(1f)
                .scrimColor(Color.BLACK)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true | false)
                .build();
        slidr.attach(this, config);

        init();
    }

    private void init() {

        Bundle data = getIntent().getExtras();
        ArrayList<String> channels = data.getStringArrayList("channels");

        items.clear();
        otherItems.clear();

        String[] allChannels = getResources().getStringArray(R.array.category_tab_title);



        for (String name: channels) {
            ChannelEntity entity = new ChannelEntity();
            entity.setName(name);
            items.add(entity);
        }




        for (String name : allChannels) {
            if(!channels.contains(name)) {
                ChannelEntity entity = new ChannelEntity();
                entity.setName(name);
                otherItems.add(entity);
            }
        }

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecy.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        final ChannelAdapter adapter = new ChannelAdapter(this, helper, items, otherItems);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER || viewType == ChannelAdapter.TYPE_FIX? 1 : 4;
            }
        });
        mRecy.setAdapter(adapter);

        adapter.setOnMyChannelItemClickListener(new ChannelAdapter.OnMyChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(ChannelActivity.this, items.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendData();
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("press");
        Toast.makeText(this, "PressBtn", Toast.LENGTH_SHORT);
        sendData();
        super.onBackPressed();
        Animatoo.animateSlideDown(this);
    }

    public void sendData()
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        ArrayList<String> channels = new ArrayList<>();


        for(ChannelEntity entity : items)
            channels.add(entity.getName());
        bundle.putStringArrayList("rtnChannels", channels);

        intent.putExtras(bundle);
        setResult(10008, intent);

    }
}
