package com.example.asg01;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.example.asg01.entity.User;
import com.example.asg01.receiver.InternetReceiver;
import com.example.asg01.service.MusicMediaService;
import com.example.asg01.service.MusicMediaServiceConnection;

public class GameOverActivity extends AppCompatActivity {
    private MusicMediaService musicService;
    private MusicMediaServiceConnection mediaServiceConnection = new MusicMediaServiceConnection();
    private InternetReceiver internetReceiver;

    private ComplexButtonFragment buttonFragment;
    private TextView score;
    @Override
    public void onBackPressed() {
        //ignore
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Button backButton = findViewById(R.id.button);
        score = findViewById(R.id.score);
        score.setText(String.valueOf(getIntent().getIntExtra("score", 0)));

        buttonFragment = new ComplexButtonFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentView, buttonFragment).commit();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                intent.putExtra("user", buttonFragment.getUser());
                startActivity(intent);
            }
        });



        internetReceiver = new InternetReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(GameOverActivity.this, MusicMediaService.class);
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