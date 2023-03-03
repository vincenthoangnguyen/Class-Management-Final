package com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.model.DetailInformationActivity;
import com.vincent.hoangnguyen.classmanagement.model.Student;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText,confirmPasswordEditText,name_EditText,id_EditText,phoneNumber_EditText;
    private Button createAccountBtn;
    private ProgressBar progressBar;
    private TextView loginBtnTextView;
    Student user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_acitivy);
        mapping();
        createAccountBtn.setOnClickListener(v -> createAccount());
        loginBtnTextView.setOnClickListener(v -> openLoginActivity());
    }

    private void openLoginActivity() {
        startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
    }

    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirm_password = confirmPasswordEditText.getText().toString().trim();
        String userName = name_EditText.getText().toString().trim();
        String id = id_EditText.getText().toString().trim();
        String phoneNumber = phoneNumber_EditText.getText().toString().trim();
        // Sau khi tạo tài khoản thành công thì bắt đầu lấy các thông tin của người dùng đó và lưu lên firebase
        // khởi tạo Object student để sẵn sàng lưu thông tin sinh viên lên firebase
        user = new Student();
        user.setTimestamp(Timestamp.now());
        user.setPhoneNumber(phoneNumber);
        user.setId(id_EditText.getText().toString().trim());
        user.setName(name_EditText.getText().toString().trim());
        user.setEmail(emailEditText.getText().toString().trim());

        boolean isValidated = validateData(email,password,confirm_password,userName,id,phoneNumber);
        if(!isValidated){  // nếu xác thực là false thì nhập lại nếu đúng thì đi tiếp
            return;
        }

        // lưu thông tin người dùng vào bộ nhớ để điền vào màn thông tin người dùng
        SharedPreferences sharedPref = getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userName", name_EditText.getText().toString().trim());
        editor.putString("userEmail",emailEditText.getText().toString().trim() );
        editor.putString("userId",id_EditText.getText().toString().trim());
        editor.putString("userPhoneNumber",phoneNumber_EditText.getText().toString().trim());
        editor.apply();
        // nếu mà isValidated đúng thì gọi tiếp phương thức tạo account trên firebase
        createAccountInFireBase(email,password);
    }

    private void createAccountInFireBase(String email, String password) {
        // khi tạo tài khoản thì gọi phương thức changInProgress biểu thị là đang trong quá trình tạo tài khoản
        changInProgress(true);
        // gọi phương thức xác thực firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // firebaseAuth.createUserWithEmailAndPassword(email,password);
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // done
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                    firebaseAuth.signOut();  // signOut account  người dùng phải verify email mới đăng nhập lại đc
                    saveInformationStudentToList(user);
                    Utility.showToast(CreateAccountActivity.this,getString(R.string.toast_CreateAccountSuccess));
                    finish();  // tắt màn hình hiện tại chuyển đến màn hình login
                }
                else {
                    //failure
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                    // getLocalizedMessage để nhận lí do tại sao tài khoản ko đc tạo
                    changInProgress(false);
                }
            }
        });

    }


    private void saveInformationStudentToList(Student user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("ListStudent").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                changInProgress(false);
                if(task.isSuccessful()){

                }
            }
        });
    }


    // tạo một phương thức nếu mà đang tạo tài khoản trên firebase thì button đc thay bằng progress bar để tránh việc người dùng tưởng lag
    void changInProgress(boolean inProgress){
        if(inProgress){
            createAccountBtn.setVisibility(View.GONE);  // đang trong tiến trình tạo account thì button biến mất
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateData(String email, String password, String confirmPassword, String name,String id,String phoneNumber){
        // validate the data that are input by user
        if( ! Patterns.EMAIL_ADDRESS.matcher(email).matches()){  // if the email invalid
            emailEditText.setError(getString(R.string.Error_Email));
            return false;
        }
        if (password.length() < 6){
            passwordEditText.setError(getString(R.string.Error_lengthPassword));
            return false;
        }
        if(! confirmPassword.equals(password)){  // 2 password don't match
            confirmPasswordEditText.setError(getString(R.string.Error_passwordNotMatch));
            return false;
        }
        if (name.isEmpty()) {
            name_EditText.setError(getString(R.string.Error_compulsory));
            return false;
        }

        if (id.isEmpty()) {
            id_EditText.setError(getString(R.string.Error_compulsory));
            return false;
        }
        if (phoneNumber.isEmpty()) {
            phoneNumber_EditText.setError(getString(R.string.Error_compulsory));
            return false;
        }
        return true;
    }
    public void deleteInformationOnFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForStudent().document(DetailInformationActivity.docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                }else {
                    Log.d("hoangdz",Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }

            }
        });
    }
    private void mapping() {
        emailEditText = findViewById(R.id.signIn_email);
        passwordEditText = findViewById(R.id.signIn_password);
        confirmPasswordEditText = findViewById(R.id.signIn_confirmPassword);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.signIn_progress_bar);
        loginBtnTextView = findViewById(R.id.login_Text_view_btn);
        name_EditText = findViewById(R.id.signIn_FullName);
        id_EditText = findViewById(R.id.signIn_id);
        phoneNumber_EditText = findViewById(R.id.signIn_PhoneNumber);
    }
}