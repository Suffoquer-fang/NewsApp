package com.example.Fangyan.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.Fangyan.R;

import java.util.Timer;
import java.util.TimerTask;


public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                Animatoo.animateSlideLeft(StartActivity.this);
                StartActivity.this.finish();
            }
        }, 3000);
    }
}