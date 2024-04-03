package com.example.asg01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.asg01.entity.User;

import java.io.ByteArrayOutputStream;

public class UserInfoFragment extends Fragment {
    private User user;
    private ImageView avt;
    private TextView name;

    private AvatarDialogFragment.OnImageSelectedListener onImageSelectedListener = new AvatarDialogFragment.OnImageSelectedListener() {
        @Override
        public void onImageSelected(int imageResId) {
            avt.setImageResource(imageResId);
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
        // Inflate the layout for this fragment
        avt = rootView.findViewById(R.id.avt);
        name = rootView.findViewById(R.id.textView3);

        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvatarDialogFragment dialogFragment = new AvatarDialogFragment();
                dialogFragment.setOnImageSelectedListener(onImageSelectedListener);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "image_dialog");
            }
        });

        user = (User) (getActivity().getIntent().getSerializableExtra("user"));
        name.setText("Hi,\n" + user.getFullname());
        return rootView;
    }
}