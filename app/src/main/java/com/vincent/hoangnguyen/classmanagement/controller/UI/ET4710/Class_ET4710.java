package com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vincent.hoangnguyen.classmanagement.model.InformationStudentActivity;
import com.vincent.hoangnguyen.classmanagement.R;

public class Class_ET4710 extends AppCompatActivity {
    public static final int BOOLEAN_REQUEST = 123;
    Button addInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_et4710);
    }


    public void setInformationForStudent(View view) {
        Intent intent = new Intent(Class_ET4710.this, InformationStudentActivity.class);
        startActivityForResult(intent, BOOLEAN_REQUEST);
        addInformation = (Button) view;
        Log.d("Button id", String.valueOf(view.getId()));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BOOLEAN_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                boolean clickedOk = data.getBooleanExtra("Clicked",false);
                if (clickedOk){
                    // nếu người dùng ấn lưu thông tin thì nút đó chuyển sang màu xanh
                    addInformation.setBackgroundColor(Color.GREEN);
            }
        }
    }
}

}

