package com.vincent.hoangnguyen.classmanagement.ui.Information;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.hoangnguyen.classmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate.CreateAccountActivity;
import com.vincent.hoangnguyen.classmanagement.databinding.FragmentInformationBinding;
import com.vincent.hoangnguyen.classmanagement.model.DetailInformationActivity;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class InformationFragment extends Fragment {

    private FragmentInformationBinding binding;
    private TextView nameTv,idTv,phoneNumberTv,emailTv;
    private TextView midtermScoreTv,finalScoreTv;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InformationViewModel galleryViewModel =
                new ViewModelProvider(this).get(InformationViewModel.class);
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //mapping
        nameTv = root.findViewById(R.id.fragment_information_name);
        idTv = root.findViewById(R.id.fragment_information_mssv);
        phoneNumberTv = root.findViewById(R.id.fragment_information_sdt);
        emailTv = root.findViewById(R.id.fragment_information_email);
        midtermScoreTv = root.findViewById(R.id.fragment_information_midTermScore);
        finalScoreTv = root.findViewById(R.id.fragment_information_finalTermScore);

        // l???y object user ????? l???y l???i c??c th??ng tin ng?????i d??ng v?? fill v??o fragment
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            emailTv.setText(user.getEmail());
        }
        // l???y th??ng tin t??? b??? nh??? ???ng d???ng ????? ??i???n v??o fragment th??ng tin sinh vi??n
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("mypref", MODE_PRIVATE);
        String username = sharedPref.getString("userName", "");
        String userEmail = sharedPref.getString("userEmail", "");
        String userID = sharedPref.getString("userId", "");
        String userPhoneNumber = sharedPref.getString("userPhoneNumber", "");
        // set text cho c??c text view
        nameTv.setText(username);
        emailTv.setText(userEmail);
        idTv.setText(userID);
        phoneNumberTv.setText(userPhoneNumber);
        // g???i h??m n??y ????? l???y l???i ??i???m
        Timer timerRefresh = new Timer();
        timerRefresh.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setUpScore();    }
        }, 0, 1000);

        return root;
    }
    // h??m l???y ??i???m t??? tr??n Firebase
    private void setUpScore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DocumentReference docRef = db.collection("ListStudent").document(user.getUid());
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // l???y gi?? tr??? ??i???m gi???a k?? v?? cu???i k??
                            String midtermScore = documentSnapshot.getString("midtermScore");
                            String finalScore = documentSnapshot.getString("finalScore");

                            // g??n ??i???m ???? v??o textView
                            midtermScoreTv.setText(midtermScore);
                            finalScoreTv.setText(finalScore);
                            Log.d("TAG", "midtermScore = " + midtermScore);
                            Log.d("TAG", "finalScore = " + finalScore);
                        } else {
                            // The document does not exist
                            Log.d("TAG", "The document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // The get operation failed
                        Log.w("TAG", "Error getting document", e);
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}