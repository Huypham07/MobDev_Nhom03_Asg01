package com.example.asg01;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import com.example.asg01.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class RegisterActivity extends AppCompatActivity  {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText fullName;
    private EditText phoneNumber;
    private EditText birthday;
    private Button registerButton;
    private TextView registerError;
    private TextView loginButton;
    private ProgressBar progressBar;
    private String locationName;
    private FusedLocationProviderClient fusedLocationClient;

    private boolean existPhone = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final LinearLayout linearLayout = findViewById(R.id.registerlayout);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        linearLayout.startAnimation(slideUpAnimation);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        emailEditText = findViewById(R.id.emailRegister);
        passwordEditText = findViewById(R.id.passwordRegister);
        fullName = findViewById(R.id.fullName);
        phoneNumber = findViewById(R.id.phone);
        birthday = findViewById(R.id.birthday);
        registerButton = findViewById(R.id.registerButton);
        registerError = findViewById(R.id.registerError);
        loginButton = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            getLastLocation();
        }

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkingFormat(emailEditText.getText().toString()
                        , passwordEditText.getText().toString()
                        , new User(fullName.getText().toString()
                                , phoneNumber.getText().toString()
                                , birthday.getText().toString()));
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        fullName.addTextChangedListener(afterTextChangedListener);
        phoneNumber.addTextChangedListener(afterTextChangedListener);
        birthday.addTextChangedListener(afterTextChangedListener);

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar myCalendar = Calendar.getInstance();
                new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                birthday.setText(String.format("%02d/%02d/%04d", i2, i1, i));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(emailEditText.getText().toString()
                        , passwordEditText.getText().toString()
                        , new User(fullName.getText().toString()
                                , phoneNumber.getText().toString()
                                , birthday.getText().toString(), locationName));

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLastLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            getAddressFromLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            locationName = "Unable to retrieve location";
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        new Thread(() -> {
            try {
                String baseUrl = "https://nominatim.openstreetmap.org/reverse";
                String lat = String.valueOf(latitude);
                String lon = String.valueOf(longitude);
                String urlString = baseUrl + "?format=json&lat=" + lat + "&lon=" + lon;
                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonDataBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonDataBuilder.append(line);
                }
                String jsonData = jsonDataBuilder.toString();
                reader.close();

                int cityIndex = jsonData.indexOf("city");
                if (cityIndex != -1) {
                    int commaIndex = jsonData.indexOf(",", cityIndex);
                    if (commaIndex != -1) {
                        String cityName = jsonData.substring(cityIndex + 7, commaIndex).trim();
                        if (cityName.startsWith("\"")) {
                            cityName = cityName.substring(1);
                        }
                        if (cityName.endsWith("\"")) {
                            cityName = cityName.substring(0, cityName.length() - 1);
                        }
                        final String finalCityName = cityName;
                        runOnUiThread(() -> locationName = finalCityName);
                        return;
                    }
                }
                runOnUiThread(() -> locationName = "Location not found");
            } catch (IOException e) {
                e.printStackTrace();
                locationName = "Location not found";
            }
        }).start();
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getAdminArea();
                }
                if (cityName != null && !cityName.isEmpty()) {
                    locationName = cityName;
                } else {
                    locationName = "Location not found";
                }
            } else {
                locationName = "Location not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationName = "Location not found";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                locationName = "Location permission denied";
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }

    private boolean checkingFormat(String email, String password, User user) {
        boolean check = true;
        if (email.isEmpty()) {
            emailEditText.setError("Email must not be empty");
            check = false;
        } else {
            emailEditText.setError(null);
        }
        if (password.length() < 8) {
            passwordEditText.setError("Password at least 8 characters");
            check = false;
        } else {
            passwordEditText.setError(null);
        }
        if (user.getFullname().isEmpty()) {
            fullName.setError("Full name must not empty");
            check = false;
        } else {
            fullName.setError(null);
        }
        if (user.getPhoneNumber().isEmpty()) {
            phoneNumber.setError("Phone number must not empty");
            check = false;
        } else {
            phoneNumber.setError(null);
        }
        return check;
    }

    private void register(String email, String password, User user) {
        progressBar.setVisibility(View.VISIBLE);
        if (checkingFormat(email, password, user)) {
            checkPhoneNumberExist((user.getPhoneNumber())).thenApply(exist -> {
               if (exist) {
                   Toast.makeText(getApplicationContext(), "Phone number already exists", Toast.LENGTH_LONG).show();
               } else {
                   firebaseAuth.createUserWithEmailAndPassword(email, password)
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {
                                       FirebaseUser curUser = firebaseAuth.getCurrentUser();

                                       DatabaseReference reference = database.getReference("Users");
                                       reference.child(curUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull @NotNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   progressBar.setVisibility(View.GONE);
                                                   Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                   intent.putExtra("newEmail", email);
                                                   startActivity(intent);
                                               } else {
                                                   Toast.makeText(RegisterActivity.this, "Can't add user's information", Toast.LENGTH_LONG).show();
                                                   progressBar.setVisibility(View.GONE);
                                               }
                                           }
                                       });
                                   } else {
                                       Toast.makeText(RegisterActivity.this, "Registration failed!!", Toast.LENGTH_LONG).show();
                                       progressBar.setVisibility(View.GONE);
                                   }
                               }
                           });
               }
                return null;
            });
        }
    }

    private CompletableFuture<Boolean> checkPhoneNumberExist(String phoneNumber) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        database.getReference("Users").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                boolean existPhone = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User u = ds.getValue(User.class);
                    if (phoneNumber.equals(u.getPhoneNumber())) {
                        existPhone = true;
                        break;
                    }
                }
                future.complete(existPhone);
            }
        });
        return future;
    }
}