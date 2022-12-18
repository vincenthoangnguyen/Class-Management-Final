package com.vincent.hoangnguyen.classmanagement.model;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.vincent.hoangnguyen.classmanagement.R;

import java.util.Objects;

public class InformationStudentActivity extends AppCompatActivity {
    EditText nameEdt,idEdt,kEdt;
    Button save_btn;
    ProgressBar progressBar;
    boolean inProgress =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_student);
        mapping();
        // set onclick cho button lưu thông tin học sinh
        save_btn.setOnClickListener(v-> saveInformationStudent());
    }

    private void saveInformationStudent() {
        String name = nameEdt.getText().toString();
        String id = idEdt.getText().toString();
        String k = kEdt.getText().toString();
        boolean isValidated=  validateData(name,id,k);
        if(!isValidated){
            return;
        }
        Student student = new Student();
        student.setName(name);
        student.setId(id);
        student.setK(k);
        saveStudentToFirebase(student);

    }

    private void saveStudentToFirebase(Student student) {
        changInLoginProgress(true);
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForStudent().document();
        documentReference.set(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                changInLoginProgress(false);
                if(task.isSuccessful()){
                    //notes is added
                    Utility.showToast(InformationStudentActivity.this,"Lưu thành công");
                    Intent intent = new Intent();
                    boolean clicked = true;
                    intent.putExtra("Clicked",clicked);
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    Log.d("hoangdz",Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    Utility.showToast(InformationStudentActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }

            }
        });
    }


    boolean validateData(String name, String id, String k){

        if(name == null || name.isEmpty()){
            nameEdt.setError("Bắt buộc");
            return false;
        }
        if(id == null || id.isEmpty()){
            idEdt.setError("Bắt buộc");
            return false;
        }
        if(k == null || k.isEmpty()){
            kEdt.setError("Bắt buộc");
            return false;
        }
        return true;
    }
    void changInLoginProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            save_btn.setVisibility(View.VISIBLE);
        }
    }
    private void mapping() {
        nameEdt =  findViewById(R.id.name_student);
        idEdt = findViewById(R.id.id_student);
        kEdt = findViewById(R.id.k_student);
        save_btn = findViewById(R.id.save_btn);
        progressBar = findViewById(R.id.progress_bar2);
    }


}