package com.vincent.hoangnguyen.classmanagement.controller.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710.Class_ET4710;
import com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710.Class_ET4710_Admin;
import com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate.LoginActivity;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ImageButton menu_btn;
    Timer timerRefresh;
    private String currentLanguage = "en"; // Current language is English

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadLanguage();
        menu_btn = findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(v -> showMenu());
    }

    // menu đăng xuất
    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menu_btn);
        popupMenu.getMenu().add("English");
        popupMenu.getMenu().add("Tiếng Việt");
        popupMenu.getMenu().add(R.string.logout);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Đăng xuất") || item.getTitle().equals("Logout")) {
                    // đăng xuất khỏi firebase
                    FirebaseAuth.getInstance().signOut();
                    // chuyển tới màn hình login
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
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
        setContentView(R.layout.activity_main);
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
    public void goToET4710(View view) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Hộp thoại xử lý");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_login_option);
        int rootView = R.layout.dialog_login_option;
        Button student_btn = (Button) dialog.findViewById(R.id.student_mode_btn);
        Button admin_btn = (Button) dialog.findViewById(R.id.admin_mode_btn);
        TextView message_student = (TextView) dialog.findViewById(R.id.message_student);
        TextView message_admin = (TextView) dialog.findViewById(R.id.message_admin);
        ViewGroup studentMode = (ViewGroup) dialog.findViewById(R.id.mode_student_linear);
        ViewGroup adminMode = (ViewGroup) dialog.findViewById(R.id.mode_admin_linear);
        EditText studentCode = (EditText) dialog.findViewById(R.id.studentCode_edt);
        EditText adminCode = (EditText) dialog.findViewById(R.id.adminCode_edt);
        Button okAdmin = (Button) dialog.findViewById(R.id.btn_switch_adminMode_ok);
        Button okStudent = (Button) dialog.findViewById(R.id.btn_student_ok);

        // chọn là học sinh show edit text vs text view tương ứng
        student_btn.setOnClickListener(v -> {
            message_student.setVisibility(View.VISIBLE);
            studentMode.setVisibility(View.VISIBLE);
            message_admin.setVisibility(View.GONE);
            adminMode.setVisibility(View.GONE);
        });
        // chọn giáo viên
        admin_btn.setOnClickListener(v -> {
            message_student.setVisibility(View.GONE);
            studentMode.setVisibility(View.GONE);
            message_admin.setVisibility(View.VISIBLE);
            adminMode.setVisibility(View.VISIBLE);
        });
        // set listener for ok admin
        okAdmin.setOnClickListener(v -> {
            String code = adminCode.getText().toString();
            boolean isValidated = validateData(code, adminCode);
            if (!isValidated) {
                return;
            }

            //if (code.equals("0934660554")) {
            if(code.equals("123")){
                Utility.showToast(MainActivity.this, getString(R.string.toast_welcomeTeacher));
                startActivity(new Intent(MainActivity.this, Class_ET4710_Admin.class));
            } else {
                Utility.showToast(MainActivity.this, getString(R.string.toast_youAreNotTeacher));
            }
        });
        // đọc dữ liệu trên firebase ở đây là đọc daily code được thây cài đặt
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // add time refresh để auto reset khi thầy thay đổi code ko cần phải thoát app ra vào lại
        timerRefresh = new Timer();
        timerRefresh.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // add để thêm còn get ở đây là để lấy dữ liệu từ data base
                // dùng orderBy để sắp xếp lấy đối tượng được lưu theo thời gian lưu
                // litmit để lấy một đối tượng
                firestore.collection("Daily code").orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // lấy thành công dữ liệu trên data base
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    String dailyCode = documentSnapshot.getString("DailyCode");
                                    //set ok cho student
                                    okStudent.setOnClickListener(v -> {
                                        String code = studentCode.getText().toString().trim();
                                        boolean isValidated = validateData(code, studentCode);
                                        if (!isValidated) {
                                            return;
                                        }
                                        if (code.equals(dailyCode)) {
                                            Utility.showToast(MainActivity.this, getString(R.string.toast_welcomeToClass));
                                            startActivity(new Intent(MainActivity.this, Class_ET4710.class));
                                        } else {
                                            Utility.showToast(MainActivity.this, getString(R.string.toast_dailyCodeIncorrect));
                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            // nếu lấy dữ liệu thất bạt
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utility.showToast(MainActivity.this, getString(R.string.toast_bugInTakeDailycode));
                            }
                        });
            }
        }, 0, 5000);
        // show dialog sau khi nhấn lớp ET4710
        dialog.show();
    }

    
    
    // tạo hàm kiểm tra để code không đc trống trc khi bấm ok
    boolean validateData(String code, EditText editText) {

        if (code == null || code.isEmpty()) {
            editText.setError(getString(R.string.Error_compulsory));
            return false;
        }
        return true;
    }
    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }
}