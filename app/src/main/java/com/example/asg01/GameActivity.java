package com.example.asg01;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asg01.entity.User;
import com.example.asg01.receiver.InternetReceiver;
import com.example.asg01.service.MusicMediaService;
import com.example.asg01.service.MusicMediaServiceConnection;

public class GameActivity extends AppCompatActivity {
    private User user;
    private GameView gameView;
    private InternetReceiver internetReceiver;


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
        gameView = new GameView(this, point.x, point.y).addGameEvent(new GameEvent() {
            @Override
            public void gameOver() {
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("score", gameView.getScore());
                startActivity(intent);
            }
        });
        setContentView(gameView);
        ImageView pauseBtn = new ImageView(this);
        pauseBtn.setImageResource(R.drawable.pause);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(GameActivity.this);
                dialog.setContentView(R.layout.layout_change_mode);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.double_line_rectangle);
                dialog.getWindow().getAttributes().windowAnimations = R.style.fadeAnimation;
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        gameView.resume();
                    }
                });
                Button back = dialog.findViewById(R.id.back);
                Switch changeMode = dialog.findViewById(R.id.switch1);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                });
                changeMode.setChecked((gameView.getMode() == GameView.TOUCH_MODE) ? false : true);
                changeMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            gameView.setMode(GameView.SENSOR_MODE);
                        } else {
                            gameView.setMode(GameView.TOUCH_MODE);
                        }
                    }
                });
                dialog.show();
                gameView.pause();
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 20;
        layoutParams.leftMargin = 20;

        addContentView(pauseBtn, layoutParams);

        internetReceiver = new InternetReceiver();
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
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicService != null) {
            musicService.pauseMedia();
        }
        unbindService(mediaServiceConnection);
        unregisterReceiver(internetReceiver);
    }
}
