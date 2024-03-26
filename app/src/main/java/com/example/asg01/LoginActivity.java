package com.example.asg01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import com.example.asg01.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerButton;
    private TextView forgotPasswordButton;
    private TextView loginError;
    private String oldEmail;
    private String oldPassword;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //create animation
        final LinearLayout linearLayout = findViewById(R.id.loginlayout);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        linearLayout.startAnimation(slideUpAnimation);

        // get firebaseAuth and DBreference
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // assign
        passwordEditText = findViewById(R.id.passwordLogin);
        emailEditText = findViewById(R.id.emailLogin);
        loginButton = findViewById(R.id.loginButton);
        loginError = findViewById(R.id.loginError);
        forgotPasswordButton = findViewById(R.id.forgotPassword);
        registerButton = findViewById(R.id.registerBtn);
        progressBar = findViewById(R.id.progressBar);

        // sharedPref
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    login(emailEditText.getText().toString(), passwordEditText.getText().toString());
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
            if (intent.getBooleanExtra("islogout", false)) {
                editor.clear();
                editor.apply();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        oldEmail = sharedPreferences.getString("email", "");
        oldPassword = sharedPreferences.getString("password", "");

        if (!oldPassword.isEmpty() && !oldEmail.isEmpty()) {
            emailEditText.setText(oldEmail);
            passwordEditText.setText(oldPassword);
            login(oldEmail, oldPassword);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth.getCurrentUser() != null) {
            editor.putString("email", emailEditText.getText().toString());
            editor.putString("password", passwordEditText.getText().toString());
            editor.apply();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }

    private boolean checkingFormat(String email, String password) {
        boolean check = true;
        if (email.isEmpty()) {
            emailEditText.setError("Email must not be empty");
            check = false;
        } else {
            emailEditText.setError(null);
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password must not be empty");
            check = false;
        } else {
            passwordEditText.setError(null);
        }
        loginButton.setEnabled(check);
        return check;
    }

    private void login(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if (checkingFormat(email, password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String UID = firebaseAuth.getCurrentUser().getUid();
                                database.getReference("Users").child(UID).get()
                                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    User user = task.getResult().getValue(User.class);
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.putExtra("user", user);
                                                    startActivity(intent);
                                                } else {
                                                    Log.e("firebase", "Error getting data", task.getException());
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            } else {
                                loginError.setText("Incorrect email or password");
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }
}