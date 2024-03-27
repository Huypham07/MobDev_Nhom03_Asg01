package com.example.asg01;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceView;
import com.example.asg01.entity.Background;
import com.example.asg01.entity.Bullet;
import com.example.asg01.entity.Enemy;
import com.example.asg01.entity.Ship;
import com.example.asg01.sensor.AccelerometerSensor;
import com.example.asg01.sensor.CustomEventSensor;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{
    private GameEvent gameEvent;
    private boolean isPlaying;
    private static final int REWARD_POINT = 10;
    private int score = 0;
    public static int screenX, screenY;
    private Thread thread;
    private final int TARGET_FPS = 60;
    private final long MAX_FRAME_TIME = 1000 / TARGET_FPS;
    private final Paint paint;
    private final Paint paintText;
    private int diffX = 0, diffY = 0;
    private float xVel = 0, yVel = 0;
    private final float MAX_VELOCITY = 1500f, MIN_VELOCITY = -1500f;
    private static int mode = 1;
    public static final int TOUCH_MODE = 1;
    public static final int SENSOR_MODE = 2;

    //sensor
    private AccelerometerSensor accelerometerSensor;

    //entities
    private final Background backGround1, backGround2;
    private final Ship ship;
    private final ArrayList<Bullet> playerBullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Bullet> enemyBullets = new ArrayList<>();

    private int cntPlayerBullet = 0;
    private int cntEnemyBullet1 = 0;
    private int cntEnemyBullet2 = 0;
    private SoundPool soundPool;
    int soundID[] = new int[5];

    private final SharedPreferences sharedPreferences;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        sharedPreferences = context.getSharedPreferences("MySharedPref",Context.MODE_PRIVATE);

        GameView.screenX = screenX;
        GameView.screenY = screenY;

        paint = new Paint();
        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(50);


        backGround1 = new Background(getResources());
        backGround2 = new Background(getResources());
        backGround1.setY(-screenY);
        backGround2.setY(0);

        ship = new Ship(getResources());
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundID[0] = soundPool.load(context, R.raw.fx_bullet, 1);

        accelerometerSensor = new AccelerometerSensor(context);
        accelerometerSensor.addCustomEventSensor(new CustomEventSensor() {
            @Override
            public void moveShip(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    if (mode == SENSOR_MODE) {
                        float time = (float) MAX_FRAME_TIME / 1000;
                        float accelX = sensorEvent.values[0] * 10000; // increase accel 10 times
                        float accelY = sensorEvent.values[1] * 10000;

                        xVel += accelX * time;
                        yVel -= accelY * time;

                        if (xVel >= MAX_VELOCITY) {
                            xVel = MAX_VELOCITY;
                        } else if (xVel <= MIN_VELOCITY) {
                            xVel = MIN_VELOCITY;
                        }

                        if (yVel >= MAX_VELOCITY) {
                            yVel = MAX_VELOCITY;
                        } else if (yVel <= MIN_VELOCITY) {
                            yVel = MIN_VELOCITY;
                        }

                        float xS = xVel * time / 2;
                        float yS = yVel * time / 2;
                        int desX = ship.getX() - (int) xS;
                        int desY = ship.getY() - (int) yS;

                        if (desX + ship.getW() >= screenX) {
                            desX = screenX - ship.getW();
                            xVel = 0;
                        } else if (desX <= 0) {
                            desX = 0;
                            xVel = 0;
                        }

                        //check collision with y
                        if (desY + ship.getH() >= screenY) {
                            desY = screenY - ship.getH();
                            yVel = 0;
                        } else if (desY <= 0) {
                            desY = 0;
                            yVel = 0;
                        }

                        ship.setX(desX);
                        ship.setY(desY);
                    }
                }
            }
        });
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
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = Math.max(0, MAX_FRAME_TIME - timeMillis);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == TARGET_FPS) {
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
        checkCollision();
        updatePlayer();
        updateEnemy();
    }

    private void draw() {
        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            //draw background
            canvas.drawBitmap(backGround1.getBackground(), backGround1.getX(), backGround1.getY(), paint);
            canvas.drawBitmap(backGround2.getBackground(), backGround2.getX(), backGround2.getY(), paint);
            //draw entity
            canvas.drawBitmap(ship.getSkin(), ship.getX(), ship.getY(), paint);

            try {
                for (Bullet bullet : playerBullets) {
                    canvas.drawBitmap(bullet.getBullet(), bullet.getX(), bullet.getY(), paint);
                }

                for (Enemy enemy : enemies) {
                    canvas.drawBitmap(enemy.getSkin(), enemy.getX(), enemy.getY(), paint);
                }

                for (Bullet bullet : enemyBullets) {
                    canvas.drawBitmap(bullet.getBullet(), bullet.getX(), bullet.getY(), paint);
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            canvas.drawText("Score: " + String.valueOf(score), 200, 80, paintText);
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
        accelerometerSensor.startListening();
        thread = new Thread(this) ;
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            accelerometerSensor.stopListening();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mode == TOUCH_MODE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    diffX = (int) event.getX() - ship.getX() - ship.getW()/2;
                    diffY = (int) event.getY() - ship.getY() - ship.getH()/2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int desX = (int) event.getX() - diffX - ship.getW()/2;
                    int desY = (int) event.getY() - diffY - ship.getH()/2;

                    //check collision with x
                    if (desX + ship.getW() >= screenX) {
                        desX = screenX - ship.getW();
                        diffX = Math.min(0, (int) event.getX() - desX - ship.getW()/2);
                    } else if (desX <= 0) {
                        desX = 0;
                        diffX = Math.max(0, (int) event.getX() - desX - ship.getW()/2);
                    }

                    //check collision with y
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
                default: break;
            }
        }

        return true;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void newPlayerBullet() {
        Bullet bullet = new Bullet(getResources(), R.drawable.bullet2);
        bullet.setH(100);
        bullet.setX(ship.getX() + (ship.getW() / 2) - (bullet.getW() / 2));
        bullet.setY(ship.getY() - ship.getH() / 4);
        playerBullets.add(bullet);
        //===========demo add sound when new bullet appear===============
        soundPool.play(soundID[0], 1, 1, 0, 0, 1);
        //=======end, maybe have different way to implement it===========
    }

    private void updatePlayer() {
        ArrayList<Bullet> unusableBullets = new ArrayList<>();
        for (Bullet bullet : playerBullets){
            if(bullet.getY() < 0){
                unusableBullets.add(bullet);
            }
            // shoot each 150 pixel after scale
            bullet.setY(bullet.getY() - 30);
        }

        for (Bullet bullet : unusableBullets) {
            playerBullets.remove(bullet);
        }

        cntPlayerBullet++;
        if (cntPlayerBullet == 10) {
            cntPlayerBullet = 0;
            newPlayerBullet();
        }
    }

    private void newEnemyBullet() {
        for (Enemy e : enemies) {
            Bullet bullet = new Bullet(getResources(), R.drawable.enemybullet);
            bullet.setH(50);
            bullet.setX(e.getX() + (e.getW() / 2) - (bullet.getW() / 2));
            bullet.setY(e.getY() + e.getH());
            enemyBullets.add(bullet);
        }
    }

    private void updateEnemy() {
        ArrayList<Bullet> unusableEnemyBullets = new ArrayList<>();
        ArrayList<Enemy> destroyedEnemies = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.getY() > screenY) {
                destroyedEnemies.add(e);
                //gameOver
                gameEvent.gameOver();
                return;
            }
            e.setY(e.getY() + 5);
        }

        for (Bullet bullet : enemyBullets){
            if(bullet.getY() + bullet.getH() < 0){
                unusableEnemyBullets.add(bullet);
            }
            bullet.setY(bullet.getY() + 10);
        }

        for (Enemy e : destroyedEnemies) {
            enemies.remove(e);
        }

        for (Bullet bullet : unusableEnemyBullets) {
            enemyBullets.remove(bullet);
        }



        cntEnemyBullet1++;
        if (cntEnemyBullet1 == 80) {
            cntEnemyBullet1 = 0;
            newEnemyBullet();
        }

        cntEnemyBullet2++;
        if (cntEnemyBullet2 == 200) {
            cntEnemyBullet2 = 0;
            Random random = new Random();
            int numEnemy = random.nextInt(5);
            for (int i = 0; i < numEnemy; i++) {
                Enemy enemy = new Enemy(getResources(), R.drawable.enemy);
                enemy.setX(random.nextInt(screenX - enemy.getW()));
                enemy.setY(random.nextInt(numEnemy) * enemy.getH() -enemy.getH());
                enemies.add(enemy);
            }
        }
    }

    private void checkCollision() {
        int x1_s = ship.getX() + 10;
        int x2_s = ship.getX() + ship.getW() - 10;

        int y1_s = ship.getY() + 10;
        int y2_s = ship.getY() + ship.getH() - 10;

        Rect rect1 = new Rect(x1_s, y1_s, x2_s, y2_s);

        for (Bullet bullet : enemyBullets) {
            int x1_b = bullet.getX() + 10;
            int x2_b = bullet.getX() + bullet.getW() - 10;

            int y1_b = bullet.getY() + 10;
            int y2_b = bullet.getY() + bullet.getH() - 10;

            Rect rect2 = new Rect(x1_b, y1_b, x2_b, y2_b);

            if (isCollision(rect1, rect2)) {
                gameEvent.gameOver();
                return;
            }
        }

        for (Enemy enemy : enemies) {
            int x1_e = enemy.getX() + 10;
            int x2_e = enemy.getX() + enemy.getW() - 10;

            int y1_e = enemy.getY() + 10;
            int y2_e = enemy.getY() + enemy.getH() - 10;

            Rect rect3 = new Rect(x1_e, y1_e, x2_e, y2_e);
            if (isCollision(rect1, rect3)) {
                gameEvent.gameOver();
                return;
            }
        }

        ArrayList<Enemy> destroyedEnemies = new ArrayList<>();
        ArrayList<Bullet> destroyedBullets = new ArrayList<>();

        for (Bullet bullet : playerBullets) {
            int x1_b = bullet.getX() + 10;
            int x2_b = bullet.getX() + bullet.getW() - 10;

            int y1_b = bullet.getY() + 10;
            int y2_b = bullet.getY() + bullet.getH() - 10;

            Rect rect4 = new Rect(x1_b, y1_b, x2_b, y2_b);

            for (Enemy enemy : enemies) {
                int x1_e = enemy.getX() + 10;
                int x2_e = enemy.getX() + enemy.getW() - 10;

                int y1_e = enemy.getY() + 10;
                int y2_e = enemy.getY() + enemy.getH() - 10;

                Rect rect5 = new Rect(x1_e, y1_e, x2_e, y2_e);
                if (isCollision(rect4, rect5)) {
                    destroyedEnemies.add(enemy);
                    destroyedBullets.add(bullet);
                    score += REWARD_POINT;
                    break;
                }
            }
        }

        for (Bullet bullet : destroyedBullets) {
            playerBullets.remove(bullet);
        }

        for (Enemy e : destroyedEnemies) {
            enemies.remove(e);
        }
    }

    private boolean isCollision(Rect rect1, Rect rect2) {
        return Rect.intersects(rect1, rect2);
    }
    public int getScore() {
        return score;
    }

    public GameView addGameEvent(GameEvent gameEvent) {
        this.gameEvent = gameEvent;
        return this;
    }
}
