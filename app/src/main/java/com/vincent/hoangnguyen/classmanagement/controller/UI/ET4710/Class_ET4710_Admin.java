package com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.hoangnguyen.classmanagement.ListStudent.ListStudent;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Class_ET4710_Admin extends AppCompatActivity {
    Button setUpDailyCode_btn;
    Dialog dialog;
    Button dialog_ok_btn;
    ProgressBar progressBar;
    EditText dialog_dailyCode_edt;
    EditText dailyCode_textView;
    String dailycode_String;
    Timer timerRefreshAdmin;
    public static String TimeClosing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_et4710_admin);
        setUpDailyCode_btn = findViewById(R.id.setUp_daily_code_btn);
        dailyCode_textView =findViewById(R.id.dailyCode_tv);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_setup_daily_code);
        dialog_ok_btn= dialog.findViewById(R.id.dialog_dailyCode_ok_btn);
        dialog_dailyCode_edt= dialog.findViewById(R.id.dialog_dailyCode_Edt);
        progressBar = dialog.findViewById(R.id.progress_bar3);
        timerRefreshAdmin = new Timer();
        timerRefreshAdmin.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("Daily code").orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // lấy thành công dữ liệu trên data base
                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                    dailycode_String = documentSnapshot.getString("DailyCode");
                                    dailyCode_textView.setText("Code buổi học hôm nay là: " + dailycode_String);
                                    dailyCode_textView.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            // nếu lấy dữ liệu thất bại
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        },0, 500);
       if(savedInstanceState!= null){
           // giữ nguyên code buổi học trên textview khi cấu hình thay đổi
           FirebaseFirestore firestore = FirebaseFirestore.getInstance();
           firestore.collection("Daily code").orderBy("timestamp", Query.Direction.DESCENDING)
                   .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                           // lấy thành công dữ liệu trên data base
                           for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                              dailycode_String = documentSnapshot.getString("DailyCode");
                               dailyCode_textView.setText("Code buổi học hôm nay là: " + dailycode_String);
                               dailyCode_textView.setVisibility(View.VISIBLE);
                           }
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       // nếu lấy dữ liệu thất bại
                       @Override
                       public void onFailure(@NonNull Exception e) {
                       }
                   });
       }

        setUpDailyCode_btn.setOnClickListener(v -> {
            dialog.show();
        });
        dialog_ok_btn.setOnClickListener(v1 -> setUpDailyCodeForClass_manual());
    }

    private void setUpDailyCodeForClass_manual() {
        Timestamp timestamp = Timestamp.now();
        String dailyCode_str = dialog_dailyCode_edt.getText().toString().trim();
        // dùng map để lưu thông tin
        Map<String,Object> dailyCode = new HashMap<>();
        dailyCode.put("DailyCode",dailyCode_str);
        dailyCode.put("timestamp",Timestamp.now());
        saveDailyCodeToFirebase(dailyCode);


    }

    private void saveDailyCodeToFirebase(Map dailyCode) {
        changInLoginProgress(true);
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        // tạo 1 collection trên firebaseFirestore
        // add để thêm data vào collection đó

        db.collection("Daily code").add(dailyCode).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                changInLoginProgress(false);
                if(task.isSuccessful()){
                    Utility.showToast(Class_ET4710_Admin.this,"Cài đặt thành công code cho buổi học ngày hôm nay!");
                    dailycode_String = Objects.requireNonNull(dailyCode.get("DailyCode")).toString();
                    dailyCode_textView.setText("Code buổi học hôm nay là:" + dailycode_String);
                    dailyCode_textView.setVisibility(View.VISIBLE);
                    // nếu lưu thành công thì dùng phương thước dissmiss để không hiển thị
                    dialog.dismiss();
                }
                else{
                    Utility.showToast(Class_ET4710_Admin.this,task.getException().getLocalizedMessage());
                }
            }
        });


    }
    // hàm onclick cho button sinh code tự động
    public void autoGenCode(View view) {
        int leftLimit = 97; // bắt đầu từ kí tự 'a'
        int rightLimit = 122; // kết thúc bằng kí tự 'z'
        int len = 6;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        // dùng map để lưu thông tin
        Map<String,Object> dailyCode = new HashMap<>();
        dailyCode.put("DailyCode",generatedString);
        dailyCode.put("timestamp",Timestamp.now());
        saveDailyCodeToFirebase(dailyCode);

    }

    void changInLoginProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            dialog_ok_btn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            dialog_ok_btn.setVisibility(View.VISIBLE);
        }
    }
    // xem thông tin sinh viên
    public void seeInformationStudent(View view) {
        Log.d("Button id_admin", String.valueOf(view.getId()));
    }
    public void openListStudent(View view) {
        startActivity(new Intent(Class_ET4710_Admin.this, ListStudent.class));
    }
    public void setUpTimeClosing(View view) {
    Dialog setTimeDialog = new Dialog(this);
    setTimeDialog.setContentView(R.layout.set_closing_time_dialog);
    EditText edt = setTimeDialog.findViewById(R.id.timeClosing_edt);
    Button ok = setTimeDialog.findViewById(R.id.ok_dialog_timeClosing);
    ok.setOnClickListener(v -> {
        TimeClosing = edt.getText().toString();
        Utility.showToast(this,"Thành công");
        setTimeDialog.dismiss();
    });
    setTimeDialog.show();
    }
}