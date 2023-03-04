package com.vincent.hoangnguyen.classmanagement.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate.CreateAccountActivity;

import java.util.HashMap;
import java.util.Map;

public class DetailInformationActivity extends AppCompatActivity {
    TextView nameTv,idTv,phoneNumberTv;
    String  name,id,phoneNumber,email,midtermScore,finalScore;
    public static String docId;
    TextView deleteTv;
    TextView email_Tv;
    ImageButton saveBtn;
    EditText midScoreEdt,finalScoreEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_information);
        //mapping
        nameTv =findViewById(R.id.detail_name);
        idTv =findViewById(R.id.detail_mssv);
        phoneNumberTv = findViewById(R.id.detail_sdt);
        deleteTv =findViewById(R.id.detail_delete_tv);
        email_Tv = findViewById(R.id.detail_email);
        saveBtn = findViewById(R.id.detail_save_btn);
        midScoreEdt = findViewById(R.id.detail_midTermScore);
        finalScoreEdt = findViewById(R.id.detail_finalTermScore);
        deleteTv.setOnClickListener(view -> deleteInformation());
        //receive data từ information adapter
        name = getIntent().getStringExtra("Name");
        id = getIntent().getStringExtra("Id");
        phoneNumber = getIntent().getStringExtra("PhoneNumber");
        email = getIntent().getStringExtra("Email");
        midtermScore = getIntent().getStringExtra("midtermScore");
        finalScore = getIntent().getStringExtra("finalScore");
        docId = getIntent().getStringExtra("docID");
        // set data
        nameTv.setText(name);
        idTv.setText(id);
        phoneNumberTv.setText(phoneNumber);
        email_Tv.setText(email);
        midScoreEdt.setText(midtermScore);
        finalScoreEdt.setText(finalScore);
        // save click cho Nút lưu điểm
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMidtermScore = midScoreEdt.getText().toString();
                String newFinalScore = finalScoreEdt.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("ListStudent").document(docId);
                /// Update the document
                Map<String, Object> updates = new HashMap<>();
                updates.put("midtermScore", newMidtermScore);
                updates.put("finalScore", newFinalScore);
                docRef.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // The document was updated successfully
                                Log.d("TAG", "Document updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // The update operation failed
                                Log.w("TAG", "Error updating document", e);
                            }
                        });
                Utility.showToast(DetailInformationActivity.this,"Lưu điểm thành công");
                finish();
            }
        });
    }

    private void deleteInformation() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.title_Notification);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage(R.string.title_QuestionBeforeDelete);
        alertDialog.setPositiveButton(R.string.title_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CreateAccountActivity createAccountActivity = new CreateAccountActivity();
                createAccountActivity.deleteInformationOnFirebase();
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.title_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }


    public void makeAMessage(View view) {
        // format string number thành số điện thoại
        String smsNumber = String.format("smsto: %s",phoneNumber);
        // tạo 1 implicit intent với tham số hành động là Action SENDTO
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("sms:" +smsNumber));
        startActivity(smsIntent);
    }
    public void makeAPhoneCall(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }
}