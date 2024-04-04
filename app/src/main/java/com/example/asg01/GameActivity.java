package com.example.asg01;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asg01.entity.User;
import com.example.asg01.receiver.InternetReceiver;
import com.example.asg01.service.MusicMediaService;
import com.example.asg01.service.MusicMediaServiceConnection;

public class GameActivity extends AppCompatActivity {
    private User user;
    private GameView gameView;
    private InternetReceiver internetReceiver;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public void onBackPressed() {
        // ignore
    }

    private MusicMediaService musicService;
    private MusicMediaServiceConnection mediaServiceConnection = new MusicMediaServiceConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (User) getIntent().getSerializableExtra("user");

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Point point = new Point();
        //set default display, get width and height to Game View
        getWindowManager().getDefaultDisplay().getSize(point);
        gameView = new GameView(this, point.x, point.y).addGameEvent(new GameEvent() {
            @Override
            public void gameOver() {
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("score", gameView.getScore());
                startActivity(intent);
            }
        });
        setContentView(gameView);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 20;
        layoutParams.leftMargin = 20;

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (SettingsActivity.onSoundtrack) {
            MainActivity.musicServiceConnection.getMusicService().playMedia();
        } else {
            MainActivity.musicServiceConnection.getMusicService().pauseMedia();
        }
        if (!SettingsActivity.onShotSound) {
            GameView.isPauseSound = true;
        }
        if (SettingsActivity.onTiltMode) {
            GameView.setMode(GameView.SENSOR_MODE);
        } else {
            GameView.setMode(GameView.TOUCH_MODE);
        }

        ImageView settingsButton = new ImageView(this);
        settingsButton.setImageResource(R.drawable.ic_settings_ingame);
        settingsButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(GameActivity.this);
            dialog.setContentView(R.layout.layout_settings_ingame);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.double_line_rectangle);
            dialog.getWindow().getAttributes().windowAnimations = R.style.fadeAnimation;
            dialog.setOnCancelListener(dialogInterface -> gameView.resume());

            Switch vibrationSwitch = dialog.findViewById(R.id.vibrationSwitch);
            Switch volumeSwitch1 = dialog.findViewById(R.id.volumeSwitch1);
            SeekBar volumeSeekBar1 = dialog.findViewById(R.id.volumeSeekBar1);
            Switch volumeSwitch2 = dialog.findViewById(R.id.volumeSwitch2);
            SeekBar volumeSeekBar2 = dialog.findViewById(R.id.volumeSeekBar2);
            volumeSeekBar2.setMax(maxVolume);
            SeekBar brightnessSeekBar = dialog.findViewById(R.id.brightnessSeekBar);
            Switch changeModeSwitch = dialog.findViewById(R.id.changeModeSwitch);
            FrameLayout exitGameButton = dialog.findViewById(R.id.exitGameButton);

            vibrationSwitch.setChecked(SettingsActivity.onVibration);

            volumeSwitch1.setChecked(SettingsActivity.onSoundtrack);
            if (!SettingsActivity.onSoundtrack) {
                MainActivity.musicServiceConnection.getMusicService().pauseMedia();
                volumeSeekBar1.setEnabled(false);
            }
            volumeSwitch1.setOnCheckedChangeListener((compoundButton, b) -> {
                SettingsActivity.onSoundtrack = b;
                if (SettingsActivity.onSoundtrack) {
                    MainActivity.musicServiceConnection.getMusicService().playMedia();
                    volumeSeekBar1.setEnabled(true);
                } else {
                    MainActivity.musicServiceConnection.getMusicService().pauseMedia();
                    volumeSeekBar1.setEnabled(false);
                }
            });
            volumeSeekBar1.setProgress(SettingsActivity.savedVolumeSoundtrack);
            volumeSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (MainActivity.musicServiceConnection.isBound()) {
                        SettingsActivity.savedVolumeSoundtrack = progress;
                        MainActivity.musicServiceConnection.getMusicService().setVolume(SettingsActivity.savedVolumeSoundtrack);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            volumeSwitch2.setChecked(SettingsActivity.onShotSound);
            if (!SettingsActivity.onShotSound) {
                GameView.isPauseSound = true;
                volumeSeekBar2.setEnabled(false);
            }
            volumeSwitch2.setOnCheckedChangeListener((compoundButton, b) -> {
                SettingsActivity.onShotSound = b;
                if (SettingsActivity.onShotSound) {
                    GameView.isPauseSound = false;
                    volumeSeekBar2.setEnabled(true);
                } else {
                    GameView.isPauseSound = true;
                    volumeSeekBar2.setEnabled(false);
                }
            });
            volumeSeekBar2.setProgress(SettingsActivity.savedVolumeShotSound);
            volumeSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    SettingsActivity.savedVolumeShotSound = progress;
                    float volumePercent = (float) SettingsActivity.savedVolumeShotSound / maxVolume;
                    GameView.setVolume(volumePercent, volumePercent);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            changeModeSwitch.setChecked(SettingsActivity.onTiltMode);
            if (SettingsActivity.onTiltMode) {
                GameView.setMode(GameView.SENSOR_MODE);
            } else {
                GameView.setMode(GameView.TOUCH_MODE);
            }
            changeModeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                SettingsActivity.onTiltMode = b;
                if (SettingsActivity.onTiltMode) {
                    GameView.setMode(GameView.SENSOR_MODE);
                } else {
                    GameView.setMode(GameView.TOUCH_MODE);
                }
            });


            brightnessSeekBar.setProgress(SettingsActivity.savedBrightness);
            brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    SettingsActivity.savedBrightness = progress;
                    boolean canWrite = Settings.System.canWrite(getApplicationContext());
                    if (canWrite) {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, SettingsActivity.savedBrightness);
                    } else {
                        Intent intent1 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        startActivity(intent1);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            exitGameButton.setOnClickListener(v1 -> {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            });

            dialog.show();
            gameView.pause();
        });

        addContentView(settingsButton, layoutParams);

        internetReceiver = new InternetReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        SettingsActivity.onVibration = sharedPreferences.getBoolean("onVibration", true);
        SettingsActivity.onSoundtrack = sharedPreferences.getBoolean("onSoundtrack", true);
        SettingsActivity.savedVolumeSoundtrack = sharedPreferences.getInt("savedVolumeSoundtrack", 0);
        SettingsActivity.onShotSound = sharedPreferences.getBoolean("onShotSound", true);
        SettingsActivity.savedVolumeShotSound = sharedPreferences.getInt("savedVolumeShotSound", 0);
        SettingsActivity.savedBrightness = sharedPreferences.getInt("savedBrightness", 0);
        SettingsActivity.onTiltMode = sharedPreferences.getBoolean("onTiltMode", true);
//        Intent intent = new Intent(this, MusicMediaService.class);
//        bindService(intent, mediaServiceConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putBoolean("onVibration", SettingsActivity.onVibration);
        editor.putBoolean("onSoundtrack", SettingsActivity.onSoundtrack);
        editor.putInt("savedVolumeSoundtrack", SettingsActivity.savedVolumeSoundtrack);
        editor.putBoolean("onShotSound", SettingsActivity.onShotSound);
        editor.putInt("savedVolumeShotSound", SettingsActivity.savedVolumeShotSound);
        editor.putInt("savedBrightness", SettingsActivity.savedBrightness);
        editor.putBoolean("onTiltMode", SettingsActivity.onTiltMode);
        editor.apply();
//        if (musicService != null) {
//            musicService.pauseMedia();
//        }
//        unbindService(mediaServiceConnection);
        unregisterReceiver(internetReceiver);
    }
}
