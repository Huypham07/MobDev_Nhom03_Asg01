package com.example.asg01;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.example.asg01.service.MusicMediaService;
import com.example.asg01.service.MusicMediaServiceConnection;

public class GameOverActivity extends AppCompatActivity {
    private MusicMediaService musicService;
    private MusicMediaServiceConnection mediaServiceConnection = new MusicMediaServiceConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentView, new ComplexButtonFragment()).commit();

        Button backButton = findViewById(R.id.button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(GameOverActivity.this, MusicMediaService.class);
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