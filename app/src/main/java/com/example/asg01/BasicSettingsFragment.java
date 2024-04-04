package com.example.asg01;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asg01.receiver.InternetReceiver;
import com.example.asg01.service.MusicMediaService;
import com.example.asg01.service.MusicMediaServiceConnection;

public class BasicSettingsFragment extends Fragment {

    private InternetReceiver internetReceiver;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public BasicSettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_settings, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        internetReceiver = new InternetReceiver();

        SettingsActivity.onVibration = sharedPreferences.getBoolean("onVibration", true);
        SettingsActivity.onSoundtrack = sharedPreferences.getBoolean("onSoundtrack", true);
        SettingsActivity.savedVolumeSoundtrack = sharedPreferences.getInt("savedVolumeSoundtrack", 0);
        SettingsActivity.onShotSound = sharedPreferences.getBoolean("onShotSound", true);
        SettingsActivity.savedVolumeShotSound = sharedPreferences.getInt("savedVolumeShotSound", 0);
        SettingsActivity.savedBrightness = sharedPreferences.getInt("savedBrightness", 0);
        if (SettingsActivity.onSoundtrack) {
            MainActivity.musicServiceConnection.getMusicService().playMedia();
        } else {
            MainActivity.musicServiceConnection.getMusicService().pauseMedia();
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(internetReceiver, filter);

        AudioManager audioManager = (AudioManager) requireActivity().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        Switch vibrationSwitch = view.findViewById(R.id.vibrationSwitch);
        Switch volumeSwitch1 = view.findViewById(R.id.volumeSwitch1);
        SeekBar volumeSeekBar1 = view.findViewById(R.id.volumeSeekBar1);
        Switch volumeSwitch2 = view.findViewById(R.id.volumeSwitch2);
        SeekBar volumeSeekBar2 = view.findViewById(R.id.volumeSeekBar2);
        volumeSeekBar2.setMax(maxVolume);
        SeekBar brightnessSeekBar = view.findViewById(R.id.brightnessSeekBar);

        vibrationSwitch.setChecked(SettingsActivity.onVibration);

        volumeSwitch1.setChecked(SettingsActivity.onSoundtrack);
        if (!SettingsActivity.onSoundtrack) {
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


        brightnessSeekBar.setProgress(SettingsActivity.savedBrightness);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingsActivity.savedBrightness = progress;
                boolean canWrite = Settings.System.canWrite(requireActivity().getApplicationContext());
                if (canWrite) {
                    Settings.System.putInt(requireActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(requireActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, SettingsActivity.savedBrightness);
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        SettingsActivity.onVibration = sharedPreferences.getBoolean("onVibration", true);
//        SettingsActivity.onSoundtrack = sharedPreferences.getBoolean("onSoundtrack", true);
//        SettingsActivity.savedVolumeSoundtrack = sharedPreferences.getInt("savedVolumeSoundtrack", 0);
//        SettingsActivity.onShotSound = sharedPreferences.getBoolean("onShotSound", true);
//        SettingsActivity.savedVolumeShotSound = sharedPreferences.getInt("savedVolumeShotSound", 0);
//        SettingsActivity.savedBrightness = sharedPreferences.getInt("savedBrightness", 0);
//        if (SettingsActivity.onSoundtrack) {
//            MainActivity.musicServiceConnection.getMusicService().playMedia();
//        } else {
//            MainActivity.musicServiceConnection.getMusicService().pauseMedia();
//        }
//
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        requireActivity().registerReceiver(internetReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        editor.putBoolean("onVibration", SettingsActivity.onVibration);
        editor.putBoolean("onSoundtrack", SettingsActivity.onSoundtrack);
        editor.putInt("savedVolumeSoundtrack", SettingsActivity.savedVolumeSoundtrack);
        editor.putBoolean("onShotSound", SettingsActivity.onShotSound);
        editor.putInt("savedVolumeShotSound", SettingsActivity.savedVolumeShotSound);
        editor.putInt("savedBrightness", SettingsActivity.savedBrightness);
        editor.apply();
        requireActivity().unregisterReceiver(internetReceiver);
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (MainActivity.musicServiceConnection.isBound()) {
//            requireActivity().unbindService(MainActivity.musicServiceConnection);
//        }
//    }
}