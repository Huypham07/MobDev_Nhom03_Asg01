package com.example.asg01;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.asg01.entity.User;

public class UserInfoFragment extends Fragment {
    private User user;
    private ImageView avt;
    private TextView name;

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

        user = (User) (getActivity().getIntent().getSerializableExtra("user"));
        name.setText("Hi,\n" + user.getFullname());
        return rootView;
    }
}