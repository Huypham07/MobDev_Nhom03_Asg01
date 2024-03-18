package com.example.asg01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerButton;
    private TextView forgotPasswordButton;
    private TextView loginError;
    private String oldEmail;
    private String oldPassword;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //create animation
        final LinearLayout linearLayout = findViewById(R.id.loginlayout);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        linearLayout.startAnimation(slideUpAnimation);

        // assign
        passwordEditText = findViewById(R.id.passwordLogin);
        emailEditText = findViewById(R.id.emailLogin);
        loginButton = findViewById(R.id.loginButton);
        loginError = findViewById(R.id.loginError);
        forgotPasswordButton = findViewById(R.id.forgotPassword);
        registerButton = findViewById(R.id.registerBtn);


        KeyBoard.setupHideKeyBoard(this, passwordEditText);
        KeyBoard.setupHideKeyBoard(this, emailEditText);

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
                checkingFormat(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String inf = intent.getStringExtra("newEmail");
            if (inf != null) {
                emailEditText.setText(inf);
            }
        }
    }

    private boolean checkingFormat(String email, String password) {
        boolean check = true;
        if (email.isEmpty()) {
            emailEditText.setError("Email must not be empty");
            check = false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password must not be empty");
            check = false;
        }
        loginButton.setEnabled(check);
        return check;
    }

    private void login(String email, String password) {
        if (checkingFormat(email, password)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}