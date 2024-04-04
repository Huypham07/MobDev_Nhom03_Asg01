package com.example.asg01.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MusicMediaServiceConnection implements ServiceConnection {

    private MusicMediaService musicService;
    private boolean isBound = false;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicMediaService.MyBinder myBinder = (MusicMediaService.MyBinder) iBinder;
        musicService = myBinder.getService();
        isBound = true;
        musicService.playMedia();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound = false;
        musicService.pauseMedia();
    }

    public MusicMediaService getMusicService() {
        return musicService;
    }


    public boolean isBound() {
        return isBound;
    }
}