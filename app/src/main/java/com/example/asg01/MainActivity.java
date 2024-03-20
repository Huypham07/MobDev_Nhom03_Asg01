package com.example.asg01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String UID;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView skinChange;
    private TextView welcome;
    private ViewFlipper viewFlipper;
    private GestureDetector gestureDetector;

    private static final int skins[] = {R.drawable.ship1, R.drawable.ship2, R.drawable.ship3, R.drawable.ship4
            , R.drawable.ship5, R.drawable.ship6, R.drawable.ship7};
    private static int curSkinNumber = 0;

    private static String[] permissionList;
    private int permissionRequestCode = 1;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 33) {
            permissionList = new String[]{
                    "Manifest.permission.READ_MEDIA_IMAGES",
                    "Manifest.permission.READ_MEDIA_VIDEO",
            };
        } else {
            permissionList = new String[]{
                    "Manifest.permission.READ_EXTERNAL_STORAGE",
                    "Manifest.permission.WRITE_EXTERNAL_STORAGE",
            };
        }
        checkAllPermission();
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

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() < e2.getX()) {
                    viewFlipper.setInAnimation(MainActivity.this, R.anim.slide_in_right);
                    viewFlipper.setOutAnimation(MainActivity.this, R.anim.slide_out_right);
                    viewFlipper.showPrevious();
                } else if (e1.getX() > e2.getX()) {
                    viewFlipper.setInAnimation(MainActivity.this, R.anim.slide_in_left);
                    viewFlipper.setOutAnimation(MainActivity.this, R.anim.slide_out_left);
                    viewFlipper.showNext();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

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


                Button save = dialog.findViewById(R.id.button2);
                Button cancel = dialog.findViewById(R.id.button5);
                viewFlipper = dialog.findViewById(R.id.viewFlipper);
                for (int i = 0; i < skins.length; i++) {
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(skins[i]);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewFlipper.addView(imageView);
                }
                while(viewFlipper.getDisplayedChild() != curSkinNumber) {
                    viewFlipper.showNext();
                }
                viewFlipper.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        gestureDetector.onTouchEvent(motionEvent);
                        return true;
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        curSkinNumber = viewFlipper.getDisplayedChild();
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

    public void checkAllPermission() {
        ArrayList<String> notGrantedPermission = new ArrayList<>();
        for (String permission : permissionList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermission.add(permission);
            }
        }
        if (!notGrantedPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, notGrantedPermission.toArray(new String[notGrantedPermission.size()])
                        , permissionRequestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode) {
            boolean notFullGranted = false;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    notFullGranted = true;
                    break;
                }
            }
            if (notFullGranted) {
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        new MaterialAlertDialogBuilder(this).setMessage("You need to access permission in Settings")
                                .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                                        startActivity(intent);
                                    }
                                }).create().show();
                        break;
                    }
                }
            }
        }
    }
}