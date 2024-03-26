package com.example.asg01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.asg01.entity.Background;
import com.example.asg01.entity.Bullet;
import com.example.asg01.entity.Ship;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable{
    private boolean isPlaying;
    public static int screenX, screenY;
    private Thread thread;
    private final int TARGET_FPS = 60;
    private final long MAX_FRAME_TIME = 1000 / TARGET_FPS;
    private final Paint paint;
    private int diffX = 0, diffY = 0;
    private boolean isMoving = false;

    //entities
    private final Background backGround1, backGround2;
    private final Ship ship;
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Bullet> unusableBullets = new ArrayList<>();
    private int cnt = 0;
    private SoundPool soundPool;
    int soundID[] = new int[5];

    private final SharedPreferences sharedPreferences;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        sharedPreferences = context.getSharedPreferences("MySharedPref",Context.MODE_PRIVATE);

        GameView.screenX = screenX;
        GameView.screenY = screenY;

        paint = new Paint();

        backGround1 = new Background(getResources());
        backGround2 = new Background(getResources());
        backGround1.setY(-screenY);
        backGround2.setY(0);

        ship = new Ship(getResources());
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundID[0] = soundPool.load(context, R.raw.fx_bullet, 1);
    }


    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;

        while (isPlaying) {
            startTime = System.nanoTime();
            update();
            draw();
            timeMillis = (System.nanoTime() - startTime) / 1000000; // Thời gian đã trôi qua từ khi bắt đầu vòng lặp
            waitTime = Math.max(0, MAX_FRAME_TIME - timeMillis); // Thời gian nghỉ giữa các frame
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == TARGET_FPS) {
                // Tính toán và hiển thị FPS trung bình
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    private void update() {
        int y1 = backGround1.getY() + 5;
        int y2 = backGround2.getY() + 5;

        backGround1.setY(y1);
        backGround2.setY(y2);

        // if the backgr was out of the screen, set y = - screen height.
        if(y1 >= screenY){
            backGround1.setY(backGround2.getY() - screenY);

        }
        if(y2  >=  screenY){
            backGround2.setY(backGround1.getY()-screenY);
        }

        for (Bullet bullet : bullets){
            if(bullet.getY() < 0){
                unusableBullets.add(bullet);
            }
            // shoot each 150 pixel after scale
            bullet.setY(bullet.getY() - 30);
        }

        for (Bullet bullet : unusableBullets) {
            bullets.remove(bullet);
        }

        if (isInTouchMode()) {
            cnt++;
            if (cnt == 10) {
                cnt = 0;
                newBullet();
            }
        }
    }

    private void draw() {
        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            //draw background
            canvas.drawBitmap(backGround1.getBackground(), backGround1.getX(), backGround1.getY(), paint);
            canvas.drawBitmap(backGround2.getBackground(), backGround2.getX(), backGround2.getY(), paint);
            //draw ship
            canvas.drawBitmap(ship.getSkin(), ship.getX(), ship.getY(), paint);
            for(Bullet bullet: bullets){
                canvas.drawBitmap(bullet.getBullet(), bullet.getX(), bullet.getY(), paint);
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true ;
        thread = new Thread(this) ;
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

@Override
public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            diffX = (int) event.getX() - ship.getX() - ship.getW()/2;
            diffY = (int) event.getY() - ship.getY() - ship.getH()/2;
            isMoving = true;
            break;
        case MotionEvent.ACTION_MOVE:
            int desX = (int) event.getX() - diffX - ship.getW()/2;
            int desY = (int) event.getY() - diffY - ship.getH()/2;
            if (desX + ship.getW() >= screenX) {
                desX = screenX - ship.getW();
                diffX = Math.min(0, (int) event.getX() - desX - ship.getW()/2);
            } else if (desX <= 0) {
                desX = 0;
                diffX = Math.max(0, (int) event.getX() - desX - ship.getW()/2);
            }


            if (desY + ship.getH() >= screenY) {
                desY = screenY - ship.getH();
                diffY = Math.min(0, (int) event.getY() - desY - ship.getH()/2);
            } else if (desY <= 0) {
                desY = 0;
                diffY = Math.max(0, (int) event.getY() - desY - ship.getH()/2);
            }


            ship.setX(desX);
            ship.setY(desY);
            break;
        case MotionEvent.ACTION_UP:
            isMoving = false;
            break;
        default: break;
    }
    return true;
}

    public void newBullet(){
        Bullet bullet = new Bullet(getResources());
        bullet.setX((int) ((ship.getX()  ) + (ship.getW() / 2) - (bullet.getW() / 2)));
        bullet.setY(ship.getY() - ship.getW() / 4);
        bullets.add(bullet);
        //===========demo add sound when new bullet appear===============
        soundPool.play(soundID[0], 1, 1, 0, 0, 1);
        //=======end, maybe have different way to implement it===========
    }
}
