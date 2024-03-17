package com.example.asg01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;
import com.example.asg01.entity.Background;
import com.example.asg01.entity.Bullet;
import com.example.asg01.entity.Ship;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable{
    private boolean isPlaying;
    public static int screenX, screenY;
    private Thread thread;
    private final Paint paint;

    //entities
    private final Background backGround1, backGround2;
    private final Ship ship;
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Bullet> unusableBullets = new ArrayList<>();
    private int cnt = 0;


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
    }


    @Override
    public void run() {
        while (isPlaying){
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        int y1 = backGround1.getY() + 5;
        int y2 = backGround2.getY() + 5;

        backGround1.setY(y1);
        backGround2.setY(y2);

        // if the backgr was out of the screen, set y = - screen height.
        if(y1 >= screenY){
            backGround1.setY(-screenY);

        }
        if(y2  >=  screenY){
            backGround2.setY(-screenY);
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
            Thread.sleep(10);
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
        int actionEvent = event.getAction();
        float X = event.getX();
        float Y = event.getY();
        switch (actionEvent) {
            case MotionEvent.ACTION_MOVE:
                if(event.getY() <= screenY && event.getX() <= screenX){
                    ship.setX((int) X - ship.getW() / 2);
                    ship.setY((int) Y - ship.getH() / 2);
                }
                break;
            case MotionEvent.ACTION_DOWN:

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
    }
}
