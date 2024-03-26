package com.example.asg01.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MusicMediaServiceConnection implements ServiceConnection {
    MusicMediaService musicService;
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicMediaService.MyBinder myBinder = (MusicMediaService.MyBinder) iBinder;
        musicService = myBinder.getService();
        musicService.playMedia();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService.pauseMedia();
    }
}
