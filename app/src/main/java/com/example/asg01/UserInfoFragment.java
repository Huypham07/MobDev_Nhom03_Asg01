package com.example.asg01;

import static androidx.core.content.ContextCompat.registerReceiver;
import static com.example.asg01.ImageAdapter.avatars;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.asg01.entity.User;
import com.example.asg01.receiver.InternetReceiver;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public class UserInfoFragment extends Fragment {
    private User user;
    private static ImageView avt;
    private TextView name;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private InternetReceiver internetReceiver;
    public static int oldAvt;

    private AvatarDialogFragment.OnImageSelectedListener onImageSelectedListener = new AvatarDialogFragment.OnImageSelectedListener() {
        @Override
        public void onImageSelected(int imageResId) {
            avt.setImageResource(imageResId);
            oldAvt = imageResId;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        internetReceiver = new InternetReceiver();
        // Inflate the layout for this fragment
        avt = rootView.findViewById(R.id.avt);

        sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        avt.setImageResource(ImageAdapter.getCurAvt());
        name = rootView.findViewById(R.id.textView3);

        user = (User) (getActivity().getIntent().getSerializableExtra("user"));
        name.setText("Hi,\n" + user.getFullname());

        registerForContextMenu(avt);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        oldAvt = sharedPreferences.getInt("oldAvt", 0);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(internetReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        editor.putInt("oldAvt", oldAvt);
        editor.apply();

        requireActivity().unregisterReceiver(internetReceiver);
    }

    @Override
    public void onCreateContextMenu(@NonNull @NotNull ContextMenu menu, @NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Change avatar");
    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getTitle().equals("Change avatar")) {
            AvatarDialogFragment dialogFragment = new AvatarDialogFragment();
            dialogFragment.setOnImageSelectedListener(onImageSelectedListener);
            dialogFragment.show(getActivity().getSupportFragmentManager(), "image_dialog");
        }
        return true;
    }
}


