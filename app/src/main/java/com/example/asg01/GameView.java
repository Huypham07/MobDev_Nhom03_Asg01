package com.example.asg01;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.SurfaceView;

public class GameView extends SurfaceView {

    private final SharedPreferences sharedPreferences;

    public GameView(Context context) {
        super(context);

        sharedPreferences = context.getSharedPreferences("MySharedPref",Context.MODE_PRIVATE);
    }
}
