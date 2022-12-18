package com.vincent.hoangnguyen.classmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText,confirmPasswordEditText;
    private Button createAccountBtn;
    private ProgressBar progressBar;
    private TextView loginBtnTextView;
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
        boolean isValidated = validateData(email,password,confirm_password);
        if(!isValidated){  // nếu xác thực là false thì nhập lại nếu đúng thì đi tiếp
            return;
        }
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
                changInProgress(false);
                if(task.isSuccessful()){
                    // done
                    Utility.showToast(CreateAccountActivity.this, "Tạo thành công, check email để xác thực tài khoản (Vào mục thư rác:((..)");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();  // signOut account  người dùng phải verify email mới đăng nhập lại đc
                    finish();  // tắt màn hình hiện tại chuyển đến màn hình login
                }
                else {
                    //failure
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                    // getLocalizedMessage để nhận lí do tại sao tài khoản ko đc tạo
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

    private boolean validateData(String email, String password, String confirmPassword){
        // validate the data that are input by user
        if( ! Patterns.EMAIL_ADDRESS.matcher(email).matches()){  // if the email invalid
            emailEditText.setError("Email không hợp lệ");
            return false;
        }
        if (password.length() < 6){
            passwordEditText.setError("Độ dài của mật khẩu phải >6");
            return false;
        }
        if(! confirmPassword.equals(password)){  // 2 password don't match
            confirmPasswordEditText.setError("Mật khẩu không khớp nhau");
            return false;
        }
        return true;
    }

    private void mapping() {
        emailEditText = findViewById(R.id.signIn_email);
        passwordEditText = findViewById(R.id.signIn_password);
        confirmPasswordEditText = findViewById(R.id.signIn_confirmPassword);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.signIn_progress_bar);
        loginBtnTextView = findViewById(R.id.login_Text_view_btn);
    }
}