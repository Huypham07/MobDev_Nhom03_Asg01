package com.example.asg01;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;

public final class MyMediaPlayer {
    private MediaPlayer player;
    private int sound;
    private static MyMediaPlayer instance;
    private MyMediaPlayer(Context context, int src) {
        player = MediaPlayer.create(context, src);
        player.setLooping(true);
    }

    public static MyMediaPlayer getInstance(Context context, int src) {
        if (instance == null) {
            instance = new MyMediaPlayer(context, src);
        }
        return instance;
    }

    public void play() {
        player.start();
    }

    public void changeVolume(int desVol) {
        //
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }
}
