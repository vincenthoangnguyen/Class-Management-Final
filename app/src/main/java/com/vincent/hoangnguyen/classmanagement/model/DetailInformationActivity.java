package com.vincent.hoangnguyen.classmanagement.model;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vincent.hoangnguyen.classmanagement.ListStudent.SendMessageActivity;
import com.vincent.hoangnguyen.classmanagement.R;

public class DetailInformationActivity extends AppCompatActivity {
    TextView nameTv,idTv,phoneNumberTv;
    String name,id,phoneNumber;
    static String docId;
    TextView deleteTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_information);
        //mapping
        nameTv =findViewById(R.id.detail_name);
        idTv =findViewById(R.id.detail_mssv);
        phoneNumberTv = findViewById(R.id.detail_sdt);
        deleteTv =findViewById(R.id.detail_delete_tv);
        deleteTv.setOnClickListener(view -> deleteInformation());
        //receive data
        name = getIntent().getStringExtra("Name");
        id = getIntent().getStringExtra("Id");
        phoneNumber = getIntent().getStringExtra("PhoneNumber");
        docId = getIntent().getStringExtra("docID");
        // set data
        nameTv.setText(name);
        idTv.setText(id);
        phoneNumberTv.setText(phoneNumber);

    }

    private void deleteInformation() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Thông Báo!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Bạn có muốn xóa hồ sơ sinh viên này không ?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                InformationStudentActivity informationStudentActivity = new InformationStudentActivity();
                informationStudentActivity.deleteInformationOnFirebase();
                finish();
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    public void makeAMessage(View view) {
        String smsNumber = String.format("smsto: %s",phoneNumber);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("sms:" +smsNumber));
        startActivity(smsIntent);
          /* Intent intent =   new Intent(this, SendMessageActivity.class);
      intent.putExtra("sdt",phoneNumber);
        startActivity(intent);*/
    }
    public void makeAPhoneCall(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }
}