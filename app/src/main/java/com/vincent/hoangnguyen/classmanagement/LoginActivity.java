package com.vincent.hoangnguyen.classmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private EditText email_login_edittext,password_login_edittext;
    private TextView createAccount_textview_btn;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mapping();
        createAccount_textview_btn.setOnClickListener(v -> startCreateActivity());
        // set click login button
        login_btn.setOnClickListener(v -> loginUser());
    }
    private void startCreateActivity() {
        startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
    }

    private void loginUser() {
        String email = email_login_edittext.getText().toString().trim();
        String password = password_login_edittext.getText().toString().trim();

        boolean isValidated= validateData(email,password);
        if(!isValidated){
            return;
        }

        loginAccountInFirebase(email,password);
    }

    private void loginAccountInFirebase(String email, String password) {
        changInLoginProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changInLoginProgress(false);
                if (task.isSuccessful()){
                    //login is success
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        // go to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Utility.showToast(LoginActivity.this, "Email chưa được xác thực làm ơn check email để xác thực");
                    }

                }
                else{
                    // login is false
                    Utility.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }
            }
        });

    }

    private boolean validateData(String email, String password){
        if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_login_edittext.setError("Email không đúng mẫu");
            return false;
        }
        if(password.length() < 6 ){
            password_login_edittext.setError("Độ dài mật khẩu phải >6");
            return false;
        }
        return true;
    }
    void changInLoginProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }
    }

    private void mapping() {
        login_btn = findViewById(R.id.login_btn);
        email_login_edittext = findViewById(R.id.loginEmail);
        password_login_edittext = findViewById(R.id.loginPassword);
        createAccount_textview_btn = findViewById(R.id.create_account_btn_textview);
        progressBar = findViewById(R.id.login_progress_bar);

    }
}