package com.example.asg01;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import androidx.viewpager.widget.ViewPager;
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

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView skinChange;
    private TextView welcome;

    private static final int skins[] = {R.drawable.ship1, R.drawable.ship2, R.drawable.ship3, R.drawable.ship4
            , R.drawable.ship5, R.drawable.ship6, R.drawable.ship7,R.drawable.ship8
            , R.drawable.ship9};
    private static int curSkinNumber = 0;

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

        // sharedPref
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

        Dialog dialog = new Dialog(MainActivity.this);
        skinChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.skin_change);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.fadeAnimation;

//                ViewPager viewPager = dialog.findViewById(R.id.viewpage);
//                viewPager.setAdapter(new SkinAdapter());

                ImageViewPicker picker = dialog.findViewById(R.id.viewpage);
                picker.setImageResources(skins);
                Button save = dialog.findViewById(R.id.button2);
                Button cancel = dialog.findViewById(R.id.button5);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        curSkinNumber = picker.getCurrentValue();
                        skinChange.setImageResource(skins[curSkinNumber]);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        curSkinNumber = sharedPreferences.getInt("oldSkin", 0);
        skinChange.setImageResource(skins[curSkinNumber]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putInt("oldSkin", curSkinNumber);
        editor.apply();
    }

    public static int getCurrentSkin() {
        return skins[curSkinNumber];
    }
}