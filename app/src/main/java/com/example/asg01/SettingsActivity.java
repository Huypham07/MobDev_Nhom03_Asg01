package com.example.asg01;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.asg01.entity.User;
import com.example.asg01.receiver.InternetReceiver;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private User user;
    private FirebaseAuth firebaseAuth;
    public static boolean onVibration;
    public static boolean onSoundtrack;
    public static int savedVolumeSoundtrack;
    public static boolean onShotSound;
    public static int savedVolumeShotSound;
    public static int savedBrightness;
    public static boolean onTiltMode;
    private InternetReceiver internetReceiver;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        create();
    }

    public void create() {
        user = (User) getIntent().getSerializableExtra("user");
        firebaseAuth = FirebaseAuth.getInstance();
        loadFragment(new BasicSettingsFragment());


        FrameLayout exitSettingsButton = findViewById(R.id.exitSettingsBtn);
        exitSettingsButton.setOnClickListener(v1 -> {

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        TextView basicButton = findViewById(R.id.basicBtn);
        TextView controlButton = findViewById(R.id.controlBtn);
        basicButton.setOnClickListener(v -> {
            basicButton.setTextColor(ContextCompat.getColor(this, R.color.white));
            basicButton.setBackgroundResource(R.color.purple);
            controlButton.setTextColor(ContextCompat.getColor(this, R.color.purple));
            controlButton.setBackground(null);
            loadFragment(new BasicSettingsFragment());
        });


        controlButton.setOnClickListener(v -> {
            controlButton.setTextColor(ContextCompat.getColor(this, R.color.white));
            controlButton.setBackgroundResource(R.color.purple);
            basicButton.setTextColor(ContextCompat.getColor(this, R.color.purple));
            basicButton.setBackground(null);
            loadFragment(new ControlSettingsFragment());
        });

        TextView logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.putExtra("islogout", true);
            startActivity(intent);
        });
        internetReceiver = new InternetReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingsActivity.onVibration = sharedPreferences.getBoolean("onVibration", true);
        SettingsActivity.onSoundtrack = sharedPreferences.getBoolean("onSoundtrack", true);
        SettingsActivity.savedVolumeSoundtrack = sharedPreferences.getInt("savedVolumeSoundtrack", 0);
        SettingsActivity.onShotSound = sharedPreferences.getBoolean("onShotSound", true);
        SettingsActivity.savedVolumeShotSound = sharedPreferences.getInt("savedVolumeShotSound", 0);
        SettingsActivity.savedBrightness = sharedPreferences.getInt("savedBrightness", 0);
        SettingsActivity.onTiltMode = sharedPreferences.getBoolean("onTiltMode", true);
        if (SettingsActivity.onSoundtrack) {
            MainActivity.musicServiceConnection.getMusicService().playMedia();
        } else {
            MainActivity.musicServiceConnection.getMusicService().pauseMedia();
        }
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, filter);
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
        editor.putBoolean("onTiltMode", SettingsActivity.onTiltMode);
        editor.apply();
        unregisterReceiver(internetReceiver);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}