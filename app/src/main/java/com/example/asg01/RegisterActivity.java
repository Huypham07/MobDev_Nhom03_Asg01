package com.example.asg01;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private String location;

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
                                , birthday.getText().toString(), "Ha noi"));

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