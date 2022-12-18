package com.vincent.hoangnguyen.classmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    ImageButton menu_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu_btn= findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(v-> showMenu());
    }
    // menu đăng xuất
    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menu_btn);
        popupMenu.getMenu().add("Đăng xuất");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle() == "Đăng xuất"){
                    // đăng xuất khỏi firebase
                    FirebaseAuth.getInstance().signOut();
                    // chuyển tới màn hình login
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }

                return false;
            }
        });
    }
    public void goToET4710(View view) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Hộp thoại xử lý");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_login_option);
        int rootView = R.layout.dialog_login_option;
        Button student_btn = (Button) dialog.findViewById(R.id.student_mode_btn);
        Button admin_btn = (Button) dialog.findViewById(R.id.admin_mode_btn);
        TextView message_student =(TextView) dialog.findViewById(R.id.message_student);
        TextView message_admin =(TextView) dialog.findViewById(R.id.message_admin);
        ViewGroup studentMode = (ViewGroup) dialog.findViewById(R.id.mode_student_linear);
        ViewGroup adminMode = (ViewGroup) dialog.findViewById(R.id.mode_admin_linear);
        EditText studentCode = (EditText)dialog.findViewById(R.id.studentCode_edt);
        EditText adminCode = (EditText)dialog.findViewById(R.id.adminCode_edt);
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
            boolean isValidated = validateData(code,adminCode);
            if(!isValidated){
                return;
            }
            if(code.equals("0934660554")){
                Utility.showToast(MainActivity.this, "Chào Mừng Thầy Tuấn");
                startActivity(new Intent(MainActivity.this,Class_ET4710_Admin.class));
            }
            else {
                Utility.showToast(MainActivity.this,"Bạn không phải thầy Tuấn");
            }
        });
        // đọc dữ liệu trên firebase ở đây là đọc daily code được thây cài đặt
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // dùng orderBy để sắp xếp lấy đối tượng được lưu theo thời gian lưu
        // litmit để lấy một đối tượng
        // add để thêm còn get ở đây là để lấy dữ liệu từ data base
       firestore.collection("Daily code").orderBy("timestamp", Query.Direction.DESCENDING)
               .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    // lấy thành công dữ liệu trên data base
                       for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                           String dailyCode = documentSnapshot.getString("DailyCode");
                           //set ok cho student
                           okStudent.setOnClickListener(v -> {
                               String code = studentCode.getText().toString().trim();
                               boolean isValidated = validateData(code,studentCode);
                               if(!isValidated){
                                   return;
                               }

                               if(code.equals(dailyCode)){
                                   Utility.showToast(MainActivity.this, "Chào Mừng Đến với lớp học");
                                   startActivity(new Intent(MainActivity.this,Class_ET4710.class));
                               }
                               else {
                                   Utility.showToast(MainActivity.this,"Sai daily code vui lòng nhập lại");
                               }
                           });

                       }
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   // nếu lấy dữ liệu thất bạt
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Utility.showToast(MainActivity.this,"Lỗi trong quá trình lấy code hàng buổi");
                           }
                       });

        // show dialog sau khi nhấn lớp ET4710
        dialog.show();
    }
    // tạo hàm kiểm tra để code không đc trống trc khi bấm ok
    boolean validateData(String code, EditText editText){

        if(code == null || code.isEmpty()){
            editText.setError("Bắt buộc");
            return false;
        }
        return true;
    }

}