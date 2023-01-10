package com.vincent.hoangnguyen.classmanagement.ListStudent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

public class SendMessageActivity extends AppCompatActivity {
    private static final String TAG = SendMessageActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    EditText messageEdt;
    String sdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_acitivity);
        messageEdt = findViewById(R.id.send_message_edt);
        ImageView sendBtn = findViewById(R.id.imageView);
        sdt = getIntent().getStringExtra("sdt");

        sendBtn.setOnClickListener(view -> {
            String smsNumber = String.format("smsto: %s",sdt);
            String message = messageEdt.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("sms:" +smsNumber));
            intent.putExtra("message",message);
            startActivity(intent);
        });

    }
   /* public void sendMessage(){

    }
    private void checkForSmsPermission(){
        // chưa cấp quyền
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            // yêu cầu cấp quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // check xem permission was granted or not
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendMessage();
            } else {
                Utility.showToast(this, "False");
            }
        }
    }*/
}
