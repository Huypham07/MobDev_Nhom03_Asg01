package com.example.asg01.entity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import com.example.asg01.GameView;
import com.example.asg01.R;

public class Ship {
    private int x, y, w, h;
    private Bitmap skin;
    private Resources res;
    public Ship(Resources res) {
        this.res = res;
        changeSkin(R.drawable.ship1);
        x = (GameView.screenX - w) / 2;
        y = GameView.screenY - h * 3 / 2;
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

    public Bitmap getSkin() {
        return skin;
    }

    public void changeSkin(int src) {
        skin = BitmapFactory.decodeResource(res, src);
        w = skin.getWidth();
        h = skin.getHeight();
        double ratio = (double) h / w;
        w = GameView.screenX / 5;
        h = (int) (ratio * w);
        skin = Bitmap.createScaledBitmap(skin, w, h, false);
    }


}
