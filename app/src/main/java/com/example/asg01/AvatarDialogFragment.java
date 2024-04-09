package com.example.asg01;

import static com.example.asg01.ImageAdapter.avatars;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class AvatarDialogFragment extends DialogFragment {

    private OnImageSelectedListener onImageSelectedListener;

    public interface OnImageSelectedListener {
        void onImageSelected(int imageResId);
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.onImageSelectedListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Tạo và đặt GridView
        GridView gridView = new GridView(getActivity());
        gridView.setNumColumns(4); // Đặt số cột cho GridView
        gridView.setAdapter(new ImageAdapter(getActivity())); // Đặt adapter cho GridView
        builder.setView(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onImageSelectedListener != null) {
                    onImageSelectedListener.onImageSelected(avatars[position]);
                }

                dismiss();
            }
        });
        return builder.create();
    }

}
