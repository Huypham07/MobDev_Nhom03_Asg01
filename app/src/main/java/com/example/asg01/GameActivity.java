package com.example.asg01;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asg01.entity.User;
import com.example.asg01.service.MusicMediaService;
import com.example.asg01.service.MusicMediaServiceConnection;

public class GameActivity extends AppCompatActivity {
    private User user;
    private GameView gameView;



    public void onBackPressed() {
        // ignore
    }

    private MusicMediaService musicService;
    private MusicMediaServiceConnection mediaServiceConnection = new MusicMediaServiceConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (User) getIntent().getSerializableExtra("user");

        Point point = new Point();
        //set default display, get width and height to Game View
        getWindowManager().getDefaultDisplay().getSize(point);
        gameView = new GameView(this, point.x, point.y);
        setContentView(gameView);
        ImageView pauseBtn = new ImageView(this);
        pauseBtn.setImageResource(R.drawable.pause);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 20;
        layoutParams.leftMargin = 20;

        addContentView(pauseBtn, layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        Intent intent = new Intent(this, MusicMediaService.class);
        bindService(intent, mediaServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicService != null) {
            musicService.pauseMedia();
        }
        unbindService(mediaServiceConnection);
    }
}
