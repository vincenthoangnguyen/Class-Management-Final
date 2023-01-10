package com.vincent.hoangnguyen.classmanagement.model;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public  static CollectionReference getCollectionReferenceForStudent(){
        // khởi tạo người dùng để lấy id
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return FirebaseFirestore.getInstance().collection("ET4710")
                .document(currentUser.getUid()).collection("Information");
    }
    // tạo  hàm chuyển timestamp sang String
    static String timeStampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd-MM-yyyy   HH:mm:ss").format(timestamp.toDate());
    }

}
