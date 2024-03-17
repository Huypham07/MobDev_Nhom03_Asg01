package com.example.asg01.entity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.asg01.R;

public class Background {
    private int x, y;
    Bitmap background;

    public Background(int screenX, int screenY, Resources res) {
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
    }
}
