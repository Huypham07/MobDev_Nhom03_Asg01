package com.example.asg01.entity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import com.example.asg01.GameView;
import com.example.asg01.R;

public class Bullet {
    private int x, y, w, h;
    private double ratio;
    private Bitmap bullet;

    // create a bullet
    public Bullet(Resources res, int src){
        bullet = BitmapFactory.decodeResource(res, src);

        w = bullet.getWidth();
        h = bullet.getHeight();

        ratio = (double) h / w;

        bullet = Bitmap.createScaledBitmap(bullet,w,h,false);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
        h = (int) (w * ratio);
        bullet = Bitmap.createScaledBitmap(bullet,w,h,false);
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
        w = (int) (h / ratio);
        bullet = Bitmap.createScaledBitmap(bullet,w,h,false);
    }

    public Bitmap getBullet() {
        return bullet;
    }

    public void setBullet(Bitmap bullet) {
        this.bullet = bullet;
    }
}
