package com.example.asg01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComplexButton#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplexButton extends Fragment {

    private ImageView startButton;
    private ImageView settingButton;
    private ImageView rankButton;
    private ImageView shareButton;
    private ImageView donateButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    public ComplexButton() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complex_button, container, false);
        // Inflate the layout for this fragment
        startButton = rootView.findViewById(R.id.startButton);
        settingButton = rootView.findViewById(R.id.settingButton);
        rankButton = rootView.findViewById(R.id.rankButton);
        shareButton = rootView.findViewById(R.id.shareButton);
        donateButton = rootView.findViewById(R.id.donateButton);

        // get firebaseAuth and DBreference
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                startActivity(intent);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("islogout", true);
                startActivity(intent);
            }
        });
        return rootView;
    }
}