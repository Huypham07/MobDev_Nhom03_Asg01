package com.example.asg01.service;

import android.app.*;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.example.asg01.R;

public class MusicMediaService extends Service {
    private MyBinder binder = new MyBinder();
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    public MusicMediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
        mediaPlayer.setLooping(true);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMedia();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public MusicMediaService getService() {
            return MusicMediaService.this;
        }
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stopMedia() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void setVolume(int volume) {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume * maxVolume / 100, 0);
    }
}