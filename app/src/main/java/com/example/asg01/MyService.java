package com.example.asg01;

import android.app.*;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private MyBinder binder = new MyBinder();
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        playMedia();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String chanelID = "Demo foreground notification";
        NotificationChannel channel = new NotificationChannel(chanelID, chanelID, NotificationManager.IMPORTANCE_DEFAULT);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, chanelID)
                .setContentText("ASG01 DEV MOB")
                .setContentTitle("service is running")
                .setSmallIcon(R.drawable.ship4);

        playMedia();

        startForeground(1, notification.build());

        return START_NOT_STICKY;
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
        MyService getService() {
            return MyService.this;
        }
    }

    public void playMedia() {
        if (!MyMediaPlayer.getInstance(getApplicationContext(), R.raw.sound).isPlaying()) {
            MyMediaPlayer.getInstance(getApplicationContext(), R.raw.sound).play();
        }
    }

    public void pauseMedia() {
        MyMediaPlayer.getInstance(getApplicationContext(), R.raw.sound).pause();
    }

    public void stopMedia() {
        MyMediaPlayer.getInstance(getApplicationContext(), R.raw.sound).stop();
    }
}