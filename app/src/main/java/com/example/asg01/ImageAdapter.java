package com.example.asg01;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.fragment.app.FragmentActivity;

public class ImageAdapter extends BaseAdapter {
    private Context context;

    public static final int avatars[] = {R.drawable.avt1, R.drawable.avt2, R.drawable.avt3, R.drawable.avt4
            , R.drawable.avt5, R.drawable.avt6, R.drawable.avt7, R.drawable.avt8, R.drawable.avt9
    , R.drawable.avt10, R.drawable.avt11, R.drawable.avt12, R.drawable.avt13, R.drawable.avt14
    , R.drawable.avt15, R.drawable.avt16};

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return avatars.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(avatars[position]);
        return imageView;
    }
}