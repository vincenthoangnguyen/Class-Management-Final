package com.vincent.hoangnguyen.classmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Class_ET4710_Admin extends AppCompatActivity {
    Button setUpDailyCode_btn;
    Dialog dialog;
    Button dialog_ok_btn;
    ProgressBar progressBar;
    EditText dialog_dailyCode_edt;
    TextView dailyCode_textView;
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
        setUpDailyCode_btn.setOnClickListener(v -> {
            dialog.show();
        });
        dialog_ok_btn.setOnClickListener(v1 -> setUpDailyCodeForClass());
    }

    private void setUpDailyCodeForClass() {
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
        /*CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Daily Code");
        DocumentReference documentReference = collectionReference.document();
        documentReference.set(dailyCode).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                changInLoginProgress(false);
                if(task.isSuccessful()){
                    Utility.showToast(Class_ET4710_Admin.this,"Cài đặt thành công code cho buổi học ngày hôm nay!");
                    dailyCode_textView.setText("Code buổi học hôm nay là: " +dailyCode.getDailyCode());
                    dailyCode_textView.setVisibility(View.VISIBLE);
                }
                else{
                    Utility.showToast(Class_ET4710_Admin.this,task.getException().getLocalizedMessage());
                }
            }
        });*/

        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        // tạo 1 collection trên firebaseFirestore
        // add để thêm data vào collection đó
        db.collection("Daily code").add(dailyCode).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                changInLoginProgress(false);
                if(task.isSuccessful()){
                    Utility.showToast(Class_ET4710_Admin.this,"Cài đặt thành công code cho buổi học ngày hôm nay!");
                    dailyCode_textView.setText("Code buổi học hôm nay là:"  + dailyCode.get("DailyCode") );
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
}