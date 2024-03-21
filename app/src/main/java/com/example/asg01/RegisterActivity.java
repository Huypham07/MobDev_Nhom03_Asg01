package com.example.asg01;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText fullName;
    private EditText birthday;
    private Button registerButton;
    private TextView registerError;
    private TextView loginButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final LinearLayout linearLayout = findViewById(R.id.registerlayout);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        linearLayout.startAnimation(slideUpAnimation);

        // get firebaseAuth and DBreference
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        emailEditText = findViewById(R.id.emailRegister);
        passwordEditText = findViewById(R.id.passwordRegister);
        fullName = findViewById(R.id.fullName);
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
                        , birthday.getText().toString()));
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        fullName.addTextChangedListener(afterTextChangedListener);
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
                                , birthday.getText().toString()));

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
        return check;
    }

    private void register(String email, String password, User user) {
        progressBar.setVisibility(View.VISIBLE);
        if (checkingFormat(email, password, user)) {
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
    }
}