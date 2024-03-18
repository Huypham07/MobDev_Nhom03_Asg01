package com.example.asg01;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageViewPicker extends LinearLayout {
    private ImageView imageView;
    private int currentValue = 0;
    private int[] imageResources;

    public ImageViewPicker(Context context) {
        super(context);
        init();
    }

    public ImageViewPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageViewPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        imageView = new ImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        addView(imageView);
        updateImage();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentValue++;
                if (currentValue >= imageResources.length) {
                    currentValue = 0;
                }
                updateImage();
            }
        });
    }

    public void setImageResources(int[] imageResources) {
        this.imageResources = imageResources;
        updateImage();
    }

    private void updateImage() {
        if (imageResources != null && imageResources.length > 0) {
            imageView.setImageResource(imageResources[currentValue]);
        }
    }

    public int getCurrentValue() {
        return currentValue;
    }
}
