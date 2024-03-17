package com.example.asg01.entity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import com.example.asg01.GameView;
import com.example.asg01.R;

public class Bullet {
    private int x, y, w, h;
    private Bitmap bullet;

    // create a bullet
    public Bullet(Resources res){
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet2);

        w = bullet.getWidth();
        h = bullet.getHeight();

        double ratio = (double) h / w;
        h = 100;
        w = (int) (h / ratio);
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
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Bitmap getBullet() {
        return bullet;
    }

    public void setBullet(Bitmap bullet) {
        this.bullet = bullet;
    }
}
