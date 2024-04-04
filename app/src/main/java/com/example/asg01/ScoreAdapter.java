package com.example.asg01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.asg01.entity.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ScoreAdapter extends ArrayAdapter<User> {
    public ScoreAdapter(@NonNull Context context, ArrayList<User> userArrayList) {
        super(context, 0, userArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.score_info, parent, false);
        }

        User user = getItem(position);
        TextView num = itemView.findViewById(R.id.num);
        TextView name = itemView.findViewById(R.id.name);
        TextView score = itemView.findViewById(R.id.score);

        num.setText(String.valueOf(position + 1));
        name.setText(String.valueOf(user.getFullname()));
        score.setText(String.valueOf(user.getScore()));

        return itemView;
    }
}
