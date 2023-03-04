package com.vincent.hoangnguyen.classmanagement.controller.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate.LoginActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private String currentLanguage = "en";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadLanguage();
        progressBar = findViewById(R.id.progress_bar);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // lấy user hiện tại check xem nếu có user thì mở Main
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                // ko có user thì mở màn login
                if(firebaseUser == null){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                // có người dùng
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        },1000);

    }

    private void loadLanguage() {
        SharedPreferences preferences = getSharedPreferences("LanguagePref", MODE_PRIVATE);
        String language = preferences.getString("Language", "English");
        setLanguage(language);
        // dùng setContentView để có thể recreate lại activity mà ko tạo ra vòng lặp vô hạn khi dùng recreate
        setContentView(R.layout.activity_splash);
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
}