package com.example.asg01;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText fullName;
    private EditText birthday;
    private Button registerButton;
    private TextView registerError;
    private TextView loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final LinearLayout linearLayout = findViewById(R.id.registerlayout);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        linearLayout.startAnimation(slideUpAnimation);

        emailEditText = findViewById(R.id.emailRegister);
        passwordEditText = findViewById(R.id.passwordRegister);
        fullName = findViewById(R.id.fullName);
        birthday = findViewById(R.id.birthday);
        registerButton = findViewById(R.id.registerButton);
        registerError = findViewById(R.id.registerError);
        loginButton = findViewById(R.id.loginBtn);

        KeyBoard.setupHideKeyBoard(this, emailEditText);
        KeyBoard.setupHideKeyBoard(this, passwordEditText);
        KeyBoard.setupHideKeyBoard(this, fullName);

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
                checkingFormat(new User(emailEditText.getText().toString()
                        , passwordEditText.getText().toString()
                        , fullName.getText().toString()
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
                register(new User(emailEditText.getText().toString()
                        , passwordEditText.getText().toString()
                        , fullName.getText().toString()
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

    private boolean checkingFormat(User user) {
        boolean check = true;
        if (user.getEmail().isEmpty()) {
            emailEditText.setError("Email must not be empty");
            check = false;
        }
        if (user.getPassword().length() < 8) {
            passwordEditText.setError("Password at least 8 characters");
            check = false;
        }
        if (user.getFullname().isEmpty()) {
            fullName.setError("Full name must not empty");
            check = false;
        }
        return check;
    }

    private void register(User user) {
        if (checkingFormat(user)) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("newEmail", user.getEmail());
            startActivity(intent);
        }
    }
}