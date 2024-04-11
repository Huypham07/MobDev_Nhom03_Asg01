package com.example.asg01;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.asg01.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ComplexButtonFragment extends Fragment {

    private ImageView startButton;
    private ImageView settingButton;
    private ImageView rankButton;
    private ImageView shareButton;
    private ImageView infoButton;

    private User user;
    private int curScore;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private Dialog rankDialog;
    private TabHost tabHost;
    private ListView worldRank;
    private ArrayList<User> worldUserArrayList = new ArrayList<>();
    private ArrayList<User> localUserArrayList = new ArrayList<>();
    private ScoreAdapter worldScoreAdapter;

    private ListView friendRank;
    private ListView localRank;
    private ArrayList<User> friendUserArrayList = new ArrayList<>();
    private ScoreAdapter friendScoreAdapter;
    private ScoreAdapter localScoreAdapter;
    public ComplexButtonFragment() {
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
        infoButton = rootView.findViewById(R.id.infoButton);

        // get firebaseAuth and DBreference
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        user = (User) getActivity().getIntent().getSerializableExtra("user");
        curScore = getActivity().getIntent().getIntExtra("score", 0);
        if (curScore > user.getScore()) {
            updateScore();
        }

        rankDialog = new Dialog(getContext());
        rankDialog.setContentView(R.layout.layout_rank);
        rankDialog.setCancelable(false);
        rankDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rankDialog.getWindow().getAttributes().windowAnimations = R.style.fadeAnimation;

        tabHost = rankDialog.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("World");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Friend");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Local");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);

        worldRank = tabHost.findViewById(R.id.worldrank);
        worldScoreAdapter = new ScoreAdapter(getContext(), worldUserArrayList);

        friendRank = rankDialog.findViewById(R.id.friendrank);
        friendScoreAdapter = new ScoreAdapter(getContext(), friendUserArrayList);

        localRank = rankDialog.findViewById(R.id.localrank);
        localScoreAdapter = new ScoreAdapter(getContext(), localUserArrayList);

        ImageView closeBtn = rankDialog.findViewById(R.id.close_rank_layout);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rankDialog.cancel();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                Intent intent = new Intent(activity, GameActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        rankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWorldRank();
                getFriendRankFromContact(getContact());
                getLocalRank();
                rankDialog.show();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateScore() {
        user.setScore(curScore);
        Map<String, Object> update = new HashMap<>();
        update.put("score", curScore);

        String UID = firebaseAuth.getUid();
        DatabaseReference databaseReference = database.getReference("Users").child(UID);
        databaseReference.updateChildren(update)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase update", "Failure");
                    }
                });
    }

    private void getWorldRank() {
        worldUserArrayList.clear();
        database.getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User u = ds.getValue(User.class);
                        worldUserArrayList.add(u);
                    }
                    sortByScore(worldUserArrayList);
                    worldRank.setAdapter(worldScoreAdapter);
                } else {
                    Log.e("error", "can't get rank");
                }
            }
        });
    }

    private List<String> getContact() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS}
                    , 0);
        }
        List<String> contacts = new ArrayList<>();
        ContentResolver resolver = getActivity().getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null);
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                int x = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (x >= 0) {
                    String phoneNumber = cursor.getString(x);
                    contacts.add(phoneNumber.replace(" ",""));
                }
            }
        }
        return contacts;
    }

    private void sortByScore(ArrayList<User> users) {
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Integer.compare(u2.getScore(), u1.getScore());
            }
        });
    }
    private void getFriendRankFromContact(List<String> contacts) {
        friendUserArrayList.clear();
        friendUserArrayList.add(user);
        database.getReference("Users").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User u = ds.getValue(User.class);
                    if (contacts.contains(u.getPhoneNumber())) {
                        friendUserArrayList.add(u);
                    }
                }
                sortByScore(friendUserArrayList);
                friendRank.setAdapter(friendScoreAdapter);
            }
        });
    }

    private void getLocalRank() {
        localUserArrayList.clear();
        //localUserArrayList.add(user);
        database.getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User u = ds.getValue(User.class);
                        if (!u.equals(user)  && u.getPosition() != null && user.getPosition() != null && u.getPosition().equals(user.getPosition())) {
                            localUserArrayList.add(u);
                        }
                    }
                    sortByScore(localUserArrayList);
                    localRank.setAdapter(localScoreAdapter);
                } else {
                    Log.e("error", "can't get rank");
                }
            }
        });
    }



    private Bitmap screenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap scrshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return scrshot;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.e("camera", "Picture taken! :)");
                    if (data != null) {
                        Bitmap tmpImgCamera = data.getParcelableExtra("data");
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        matrix.postScale(1.5f, 1.5f);
                        Bitmap imgCamera = Bitmap.createBitmap(tmpImgCamera, 0, 0, tmpImgCamera.getWidth(), tmpImgCamera.getHeight(), matrix, true);
                        Bitmap imgGame = screenshot(getActivity().getWindow().getDecorView().getRootView());
                        Bitmap mergedBitmap = Bitmap.createBitmap(imgGame.getWidth(), imgGame.getHeight(), imgGame.getConfig());
                        Canvas canvas = new Canvas(mergedBitmap);

                        canvas.drawBitmap(imgGame, 0, 0, null);

                        int x = 750; // Thay đổi tùy ý
                        int y = 50; // Thay đổi tùy ý
                        canvas.drawBitmap(imgCamera, x, y, null);

                        imgCamera.recycle();
                        imgCamera.recycle();

                        Dialog dialog = new Dialog(getContext());
                        ImageView img = new ImageView(getContext());
                        dialog.setContentView(img);
                        img.setImageBitmap(mergedBitmap);
                        dialog.getWindow().setLayout(720, 1280);
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 5000);
                        Toast.makeText(getContext(), "The screenshot has been saved", Toast.LENGTH_LONG).show();
                        // save to device
                        Date date = new Date();
                        //formate file
                        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
                        try {
                            // File path
                            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + "Game" + "/" + "screenshot" + "-" + format + ".jpeg";
                            File file = new File(path);
                            if (!file.exists()) {
                                file.mkdir();
                            }

                            FileOutputStream outputStream = new FileOutputStream(file);
                            mergedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (FileNotFoundException io) {
                            io.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case RESULT_CANCELED:
                    Log.e("camera", "Picture canceled! :(");
                    break;
            }
        }
    }

    public User getUser() {
        return user;
    }

    public int getCurScore() {
        return curScore;
    }
}