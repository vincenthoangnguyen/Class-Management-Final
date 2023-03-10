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
        // Sau khi t???o t??i kho???n th??nh c??ng th?? b???t ?????u l???y c??c th??ng tin c???a ng?????i d??ng ???? v?? l??u l??n firebase
        // kh???i t???o Object student ????? s???n s??ng l??u th??ng tin sinh vi??n l??n firebase
        user = new Student();
        user.setTimestamp(Timestamp.now());
        user.setPhoneNumber(phoneNumber);
        user.setId(id_EditText.getText().toString().trim());
        user.setName(name_EditText.getText().toString().trim());
        user.setEmail(emailEditText.getText().toString().trim());
        user.setMidtermScore("");
        user.setFinalScore("");
        boolean isValidated = validateData(email,password,confirm_password,userName,id,phoneNumber);
        if(!isValidated){  // n???u x??c th???c l?? false th?? nh???p l???i n???u ????ng th?? ??i ti???p
            return;
        }

        // l??u th??ng tin ng?????i d??ng v??o b??? nh??? ????? ??i???n v??o m??n th??ng tin ng?????i d??ng
        SharedPreferences sharedPref = getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userName", name_EditText.getText().toString().trim());
        editor.putString("userEmail",emailEditText.getText().toString().trim() );
        editor.putString("userId",id_EditText.getText().toString().trim());
        editor.putString("userPhoneNumber",phoneNumber_EditText.getText().toString().trim());
        editor.apply();
        // n???u m?? isValidated ????ng th?? g???i ti???p ph????ng th???c t???o account tr??n firebase
        createAccountInFireBase(email,password);
    }

    private void createAccountInFireBase(String email, String password) {
        // khi t???o t??i kho???n th?? g???i ph????ng th???c changInProgress bi???u th??? l?? ??ang trong qu?? tr??nh t???o t??i kho???n
        changInProgress(true);
        // g???i ph????ng th???c x??c th???c firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // firebaseAuth.createUserWithEmailAndPassword(email,password);
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // done
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    saveInformationStudentToList(user);
                    firebaseAuth.signOut();  // signOut account  ng?????i d??ng ph???i verify email m???i ????ng nh???p l???i ??c

                    Utility.showToast(CreateAccountActivity.this,getString(R.string.toast_CreateAccountSuccess));
                    finish();  // t???t m??n h??nh hi???n t???i chuy???n ?????n m??n h??nh login
                }
                else {
                    //failure
                    Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                    // getLocalizedMessage ????? nh???n l?? do t???i sao t??i kho???n ko ??c t???o
                    changInProgress(false);
                }
            }
        });

    }


    private void saveInformationStudentToList(Student user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        assert user1 != null;
        firestore.collection("ListStudent").document(user1.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                changInProgress(false);
                finish();
            }
        });
    }


    // t???o m???t ph????ng th???c n???u m?? ??ang t???o t??i kho???n tr??n firebase th?? button ??c thay b???ng progress bar ????? tr??nh vi???c ng?????i d??ng t?????ng lag
    void changInProgress(boolean inProgress){
        if(inProgress){
            createAccountBtn.setVisibility(View.GONE);  // ??ang trong ti???n tr??nh t???o account th?? button bi???n m???t
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