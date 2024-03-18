package com.example.asg01;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String UID;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private ImageView skinChange;
    private TextView welcome;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        skinChange = findViewById(R.id.skinChange);
        welcome = findViewById(R.id.textView3);

        // get firebaseAuth and DBreference
        firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        user = (User) getIntent().getSerializableExtra("user");
        welcome.setText("Hi,\n" + user.getFullname());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentView, new ComplexButton()).commit();
        ImageView avt = findViewById(R.id.avt);
        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}