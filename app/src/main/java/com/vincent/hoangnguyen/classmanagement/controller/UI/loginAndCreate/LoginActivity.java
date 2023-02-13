package com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.controller.UI.MainActivity;
import com.vincent.hoangnguyen.classmanagement.controller.UI.SplashActivity;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private EditText email_login_edittext,password_login_edittext;
    private TextView createAccount_textview_btn;
    private ProgressBar progressBar;
    private TextView menu_language;
    private String currentLanguage = "en"; // Current language is English

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        //gọi hàm loadLanguage ngay sau khi setContentView để có thể cập nhật ngôn ngữ
        loadLanguage();
        // sau khi setUp lại ngôn ngữ và khởi tạo lại contenView thì lúc này mới tiến hành mapping lại các view
        mapping();
        createAccount_textview_btn.setOnClickListener(v -> startCreateActivity());
        // set click login button
        login_btn.setOnClickListener(v -> loginUser());

    }

    public void openMenuLanguage(View view) {
        PopupMenu menu = new PopupMenu(LoginActivity.this, menu_language);
        menu.getMenu().add("English");
        menu.getMenu().add("Tiếng Việt");
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle() == "English"){
                    setLanguage("English");
                    recreate();
                }
                if(item.getTitle() == "Tiếng Việt"){
                    setLanguage("Vietnamese");
                    recreate();
                }
                return false;
            }
        });
    }
    private void loadLanguage() {
        Log.d("LANGUAGE","Called");
        SharedPreferences preferences = getSharedPreferences("LanguagePref", MODE_PRIVATE);
        String language = preferences.getString("Language", "English");
        Log.d("LANGUAGE",language);
        setLanguage(language);
        // dùng setContentView để có thể recreate lại activity mà ko tạo ra vòng lặp vô hạn khi dùng recreate
        setContentView(R.layout.activity_login);
    }
    private void setLanguage(String language) {
        // Save the selected language using SharedPreferences
        SharedPreferences preferences = getSharedPreferences("LanguagePref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", language);
        editor.apply();
        // Update the app to reflect the selected language
        if(language.equals("English")) {
            currentLanguage = "en";
            setLocale(currentLanguage);
        }
        if (language.equals("Vietnamese")){
            currentLanguage = "vi";
            setLocale(currentLanguage);
        }

    }
    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
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
                        Utility.showToast(LoginActivity.this, getString(R.string.toast_EmailNotVerify));
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
            email_login_edittext.setError(getString(R.string.Error_Email));
            return false;
        }
        if(password.length() < 6 ){
            password_login_edittext.setError(getString(R.string.Error_lengthPassword));
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
        menu_language = findViewById(R.id.menu_tv_language);

    }


}