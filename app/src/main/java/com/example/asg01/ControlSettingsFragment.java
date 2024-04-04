package com.example.asg01;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asg01.entity.User;
import com.example.asg01.receiver.InternetReceiver;
import com.google.firebase.auth.FirebaseAuth;

public class ControlSettingsFragment extends Fragment {

    private GameView gameView;
    FrameLayout swipeModeBtn;
    FrameLayout tiltModeBtn;

    private InternetReceiver internetReceiver;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ControlSettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        swipeModeBtn = view.findViewById(R.id.swipeModeBtn);
        swipeModeBtn.setOnClickListener(v -> onChooseSwipeMode());
        tiltModeBtn = view.findViewById(R.id.tiltModeBtn);
        tiltModeBtn.setOnClickListener(v -> onChooseTiltMode());
        if (SettingsActivity.onTiltMode) {
            onChooseTiltMode();
        } else {
            onChooseSwipeMode();
        }
        internetReceiver = new InternetReceiver();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void onChooseSwipeMode() {
        SettingsActivity.onTiltMode =  false;
        swipeModeBtn.setBackgroundResource(R.drawable.image_button_choose);
        int widthInDp = 270;
        int heightInDp = 130;
        int widthInPx = dpToPx(widthInDp);
        int heightInPx = dpToPx(heightInDp);
        ViewGroup.LayoutParams layoutParams = swipeModeBtn.getLayoutParams();
        layoutParams.width = widthInPx;
        layoutParams.height = heightInPx;
        swipeModeBtn.setLayoutParams(layoutParams);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) swipeModeBtn.getLayoutParams();
        int marginInDp = 5;
        int marginInPx = dpToPx(marginInDp);
        marginLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
        swipeModeBtn.setLayoutParams(marginLayoutParams);
        onCloseTiltMode();
        GameView.setMode(GameView.TOUCH_MODE);
    }

    private void onCloseSwipeMode() {
        swipeModeBtn.setBackgroundResource(R.drawable.image_button);
        int widthInDp = 260;
        int heightInDp = 120;
        int widthInPx = dpToPx(widthInDp);
        int heightInPx = dpToPx(heightInDp);
        ViewGroup.LayoutParams layoutParams = swipeModeBtn.getLayoutParams();
        layoutParams.width = widthInPx;
        layoutParams.height = heightInPx;
        swipeModeBtn.setLayoutParams(layoutParams);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) swipeModeBtn.getLayoutParams();
        int marginInDp = 10;
        int marginInPx = dpToPx(marginInDp);
        marginLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
        swipeModeBtn.setLayoutParams(marginLayoutParams);
    }

    private void onChooseTiltMode() {
        SettingsActivity.onTiltMode = true;
        tiltModeBtn.setBackgroundResource(R.drawable.image_button_choose);
        int widthInDp = 270;
        int heightInDp = 130;
        int widthInPx = dpToPx(widthInDp);
        int heightInPx = dpToPx(heightInDp);
        ViewGroup.LayoutParams layoutParams = tiltModeBtn.getLayoutParams();
        layoutParams.width = widthInPx;
        layoutParams.height = heightInPx;
        tiltModeBtn.setLayoutParams(layoutParams);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) tiltModeBtn.getLayoutParams();
        int marginInDp = 5;
        int marginTopInDp = 145;
        int marginInPx = dpToPx(marginInDp);
        int marginTopInPx = dpToPx(marginTopInDp);
        marginLayoutParams.setMargins(marginInPx, marginTopInPx, marginInPx, marginInPx);
        tiltModeBtn.setLayoutParams(marginLayoutParams);
        onCloseSwipeMode();
        GameView.setMode(GameView.SENSOR_MODE);
    }

    private void onCloseTiltMode() {
        tiltModeBtn.setBackgroundResource(R.drawable.image_button);
        int widthInDp = 260;
        int heightInDp = 120;
        int widthInPx = dpToPx(widthInDp);
        int heightInPx = dpToPx(heightInDp);
        ViewGroup.LayoutParams layoutParams = tiltModeBtn.getLayoutParams();
        layoutParams.width = widthInPx;
        layoutParams.height = heightInPx;
        tiltModeBtn.setLayoutParams(layoutParams);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) tiltModeBtn.getLayoutParams();
        int marginInDp = 10;
        int marginTopInDp = 150;
        int marginInPx = dpToPx(marginInDp);
        int marginTopInPx = dpToPx(marginTopInDp);
        marginLayoutParams.setMargins(marginInPx, marginTopInPx, marginInPx, marginInPx);
        tiltModeBtn.setLayoutParams(marginLayoutParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingsActivity.onTiltMode = sharedPreferences.getBoolean("onTiltMode", true);
        if (SettingsActivity.onSoundtrack) {
            MainActivity.musicServiceConnection.getMusicService().playMedia();
        } else {
            MainActivity.musicServiceConnection.getMusicService().pauseMedia();
        }
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(internetReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        editor.putBoolean("onTiltMode", SettingsActivity.onTiltMode);
        editor.apply();
        requireActivity().unregisterReceiver(internetReceiver);
    }
}
