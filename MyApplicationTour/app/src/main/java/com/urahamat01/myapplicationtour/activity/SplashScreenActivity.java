package com.urahamat01.myapplicationtour.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.urahamat01.myapplicationtour.R;

public class SplashScreenActivity extends AppCompatActivity {

    TextView textView;
    ProgressBar progressBar;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progressBarId);
        textView = findViewById(R.id.splashScreenTextViewId);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        textView.setAnimation(animation);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        thread.start();
    }

    public void doWork(){

        for(progress = 1; progress <= 100; progress = progress + 1){
            try {
                Thread.sleep(30);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

